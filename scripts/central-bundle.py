#!/usr/bin/env python3
"""Build a compact Central Portal bundle from locally signed Gradle staging output."""
from pathlib import Path
import argparse
import hashlib
import re
import zipfile

ROOT = Path(__file__).resolve().parent.parent
STAGING = ROOT / "build/central-staging/io/github/chengine-project"
MODULES = ("core", "spring", "telegram", "spring-boot-starter-telegram", "session-postgres")
parser = argparse.ArgumentParser()
parser.add_argument("--version", required=True)
parser.add_argument("--output", type=Path, default=ROOT / "build/central/chengine-bundle.zip")
args = parser.parse_args()
if not re.fullmatch(r"[A-Za-z0-9][A-Za-z0-9._-]*", args.version):
    raise SystemExit("Invalid version")
files = []
for module in MODULES:
    directory = STAGING / module / args.version
    basename = f"{module}-{args.version}"
    for suffix in (".pom", ".module", ".jar", "-sources.jar", "-javadoc.jar"):
        artifact = directory / (basename + suffix)
        signature = Path(str(artifact) + ".asc")
        if not artifact.is_file() or not signature.is_file():
            raise SystemExit(f"Missing signed artifact: {artifact} or {signature}")
        files.append((artifact, signature))
args.output.parent.mkdir(parents=True, exist_ok=True)
with zipfile.ZipFile(args.output, "w", compression=zipfile.ZIP_DEFLATED) as bundle:
    for artifact, signature in files:
        relative = artifact.relative_to(ROOT / "build/central-staging").as_posix()
        data = artifact.read_bytes()
        bundle.writestr(relative, data)
        bundle.writestr(relative + ".asc", signature.read_bytes())
        bundle.writestr(relative + ".md5", hashlib.md5(data, usedforsecurity=False).hexdigest())
        bundle.writestr(relative + ".sha1", hashlib.sha1(data, usedforsecurity=False).hexdigest())
print(f"Prepared {args.output}: {len(files) * 4} files, {args.output.stat().st_size} bytes; no upload performed")
