#!/usr/bin/env python3
"""Generate a throwaway key, stage a bundle locally, and verify every signature."""
from pathlib import Path
import hashlib
import argparse
import os
import re
import shutil
import subprocess
import tempfile
import zipfile
import xml.etree.ElementTree as ET

parser = argparse.ArgumentParser()
parser.add_argument("--gpg", action="store_true", help="Test local gpg-agent signing instead of an in-memory key")
args = parser.parse_args()

root = Path(__file__).resolve().parent.parent
stage = root / "build/central-staging"
if stage.exists():
    shutil.rmtree(stage)
with tempfile.TemporaryDirectory(prefix="cpg-", dir="/tmp") as tmp:
    gpgdir = Path(tmp) / "gnupg"
    gpgdir.mkdir(mode=0o700)
    gpg = ["gpg", "--homedir", str(gpgdir), "--batch", "--pinentry-mode", "loopback"]
    subprocess.run(gpg + ["--passphrase", "", "--quick-generate-key", "Chengine Release Test <release-test@example.invalid>", "rsa2048", "sign", "1d"], check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    env = os.environ.copy()
    command = [str(root / "gradlew"), "publishAllPublicationsToCentralStagingRepository", "-PprepareCentral=true", "--no-daemon", "--offline"]
    if args.gpg:
        env["GNUPGHOME"] = str(gpgdir)
        command.extend(["-PuseLocalGpg=true", "-Psigning.gnupg.executable=gpg", "-Psigning.gnupg.keyName=release-test@example.invalid"])
    else:
        key = subprocess.run(gpg + ["--passphrase", "", "--armor", "--export-secret-keys", "release-test@example.invalid"], check=True, stdout=subprocess.PIPE).stdout.decode()
        env["CHENGINE_SIGNING_KEY"] = key
        env["CHENGINE_SIGNING_PASSWORD"] = ""
    subprocess.run(command, cwd=root, env=env, check=True)
    bundle = Path(tmp) / "bundle.zip"
    version_match = re.search(r'^\s*version\s*=\s*"([^"]+)"', (root / "build.gradle.kts").read_text(), re.MULTILINE)
    if version_match is None:
        raise SystemExit("Project version not found")
    subprocess.run(["python3", str(root / "scripts/central-bundle.py"), "--version", version_match.group(1), "--output", str(bundle)], cwd=root, check=True)
    with zipfile.ZipFile(bundle) as archive:
        names = set(archive.namelist())
        artifacts = [name for name in names if name.endswith((".jar", ".pom", ".module"))]
        if len(artifacts) != 25 or len(names) != 100:
            raise SystemExit(f"Unexpected bundle contents: {len(artifacts)} artifacts, {len(names)} files")
        total_uncompressed = sum(item.file_size for item in archive.infolist())
        for name in artifacts:
            data = archive.read(name)
            if name.endswith(".pom"):
                pom = ET.fromstring(data)
                ns = {"m": "http://maven.apache.org/POM/4.0.0"}
                if pom.findtext("m:groupId", namespaces=ns) != "io.github.chengine-project":
                    raise SystemExit(f"Incorrect groupId in {name}")
                if pom.find("m:developers/m:developer/m:name", ns) is None or pom.find("m:licenses/m:license/m:name", ns) is None:
                    raise SystemExit(f"Missing developer or license in {name}")
            for algo in ("md5", "sha1"):
                expected = hashlib.new(algo, data, usedforsecurity=False).hexdigest()
                if archive.read(name + "." + algo).decode() != expected:
                    raise SystemExit(f"Invalid {algo} for {name}")
            artifact = Path(tmp) / "artifact"
            signature = Path(tmp) / "signature.asc"
            artifact.write_bytes(data)
            signature.write_bytes(archive.read(name + ".asc"))
            subprocess.run(gpg + ["--verify", str(signature), str(artifact)], check=True, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    print(f"Verified 25 artifacts, 25 signatures, and 50 checksums; uncompressed bundle size: {total_uncompressed} bytes")
    subprocess.run(["gpgconf", "--homedir", str(gpgdir), "--kill", "gpg-agent"], check=False, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
