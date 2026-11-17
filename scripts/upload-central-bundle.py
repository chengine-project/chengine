#!/usr/bin/env python3
"""Upload a signed bundle to Central Portal for manual review and publication."""
import argparse
import base64
import os
from pathlib import Path
import re
import urllib.error
import urllib.parse
import urllib.request
import uuid

ENDPOINT = "https://central.sonatype.com/api/v1/publisher/upload"


def upload(bundle: Path, version: str, username: str, password: str) -> str:
    if not username or not password:
        raise ValueError("Set CENTRAL_PORTAL_USERNAME and CENTRAL_PORTAL_PASSWORD")
    if not re.fullmatch(r"[A-Za-z0-9][A-Za-z0-9._-]*", version):
        raise ValueError("Invalid version")
    data = bundle.read_bytes()
    boundary = "chengine-" + uuid.uuid4().hex
    prefix = (
        f"--{boundary}\r\n"
        'Content-Disposition: form-data; name="bundle"; filename="chengine-bundle.zip"\r\n'
        "Content-Type: application/octet-stream\r\n\r\n"
    ).encode("ascii")
    body = prefix + data + f"\r\n--{boundary}--\r\n".encode("ascii")
    authorization = base64.b64encode(f"{username}:{password}".encode()).decode("ascii")
    query = urllib.parse.urlencode({"name": f"chengine-{version}", "publishingType": "USER_MANAGED"})
    request = urllib.request.Request(
        f"{ENDPOINT}?{query}", data=body, method="POST",
        headers={"Authorization": f"Bearer {authorization}", "Content-Type": f"multipart/form-data; boundary={boundary}"},
    )
    try:
        with urllib.request.urlopen(request, timeout=120) as response:
            deployment = response.read(1024).decode("ascii").strip()
    except urllib.error.HTTPError as error:
        raise RuntimeError(f"Central Portal rejected the upload (HTTP {error.code}); check credentials and portal status") from None
    except (urllib.error.URLError, OSError):
        raise RuntimeError("Central Portal upload failed; check the portal for an existing deployment before retrying") from None
    try:
        return str(uuid.UUID(deployment))
    except ValueError:
        raise RuntimeError("Unexpected Central Portal response; check the portal for an existing deployment before retrying") from None


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--version", required=True)
    parser.add_argument("--bundle", type=Path, default=Path("build/central/chengine-bundle.zip"))
    args = parser.parse_args()
    deployment = upload(args.bundle, args.version, os.environ.get("CENTRAL_PORTAL_USERNAME", ""), os.environ.get("CENTRAL_PORTAL_PASSWORD", ""))
    print(f"Uploaded user-managed deployment {deployment}; review and publish it in https://central.sonatype.com/publishing/deployments")
    summary = os.environ.get("GITHUB_STEP_SUMMARY")
    if summary:
        with open(summary, "a", encoding="utf-8") as handle:
            handle.write(f"### Central Portal\nDeployment `{deployment}` was uploaded for validation. [Review it in Central Portal](https://central.sonatype.com/publishing/deployments).\n")


if __name__ == "__main__":
    main()
