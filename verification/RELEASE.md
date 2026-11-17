# Preparing a Maven Central bundle

The verified namespace is `io.github.chengine-project`. The project stages its five Maven modules together. Ordinary builds and local publications do not require signing credentials and do not contact Maven Central.

## GitHub Actions

The **Prepare Maven Central release** workflow runs only when manually dispatched on the current `main` commit. It runs the build and tests, signs all five modules, packages them and saves the bundle as a GitHub Actions artifact for seven days. It requires the repository secret `CHENGINE_SIGNING_KEY` and, if the key is protected, `CHENGINE_SIGNING_PASSWORD`; `CHENGINE_SIGNING_KEY_ID` is optional when needed for a signing subkey.

Leave **Upload to Central Portal** unchecked to prepare a bundle without uploading it. To upload for validation, add `CENTRAL_PORTAL_USERNAME` and `CENTRAL_PORTAL_PASSWORD` repository secrets containing the username and password of a [Central Portal user token](https://central.sonatype.org/publish/generate-portal-token/), then start the workflow with the option checked. These are not the GitHub or Sonatype account password. The workflow uploads a `USER_MANAGED` deployment; check its validation result and publish it separately in the Central Portal. If an upload times out or returns an unexpected response, check the Portal for an existing deployment before retrying.

## Local preparation

Run the fully local verification first:

```bash
python3 scripts/verify-central-bundle.py
```

It creates a disposable test-only GPG key, signs a local staging repository, packages the artifacts, verifies each signature and checksum, and removes the test bundle and key. No existing key or publisher token is used. The example bundle contains 100 files; actual usage is determined by Sonatype after publication.

For a real release, select the version in the root `build.gradle.kts` and ensure the public GPG key is discoverable by Maven Central. To use a key already installed in your local GPG keyring, let `gpg-agent` unlock it; do not export the private key into the repository:

```bash
./gradlew clean publishAllPublicationsToCentralStagingRepository -PprepareCentral=true -PuseLocalGpg=true -Psigning.gnupg.executable=gpg -Psigning.gnupg.keyName=YOUR_KEY_FINGERPRINT
python3 scripts/central-bundle.py --version 1.0.0
```

To use an in-memory key instead, provide these environment variables through a secure local secret manager or CI secrets, never in the repository:

- `CHENGINE_SIGNING_KEY`: ASCII-armored private key (or signing subkey).
- `CHENGINE_SIGNING_PASSWORD`: its passphrase, if any.
- `CHENGINE_SIGNING_KEY_ID`: key ID when using a signing subkey.

Do not use the disposable verification key for a real release. Keep a secure backup of the real key. With the in-memory variables set, prepare an isolated bundle (use the actual project version):

```bash
./gradlew clean publishAllPublicationsToCentralStagingRepository -PprepareCentral=true
python3 scripts/central-bundle.py --version 1.0.0
```

`build/central/chengine-bundle.zip` contains the five modules' JARs, sources, POMs, Gradle metadata, Dokka-generated HTML API documentation JARs, signatures, and required MD5/SHA-1 checksums. Review the version, artifact contents, signatures and credentials before uploading. Staging and bundling never upload or publish a release.

The bundle can be uploaded through the Central Portal as a user-managed deployment, validated, and inspected before a separate manual publish action. Published Maven Central versions cannot be changed. Follow [the Central Portal upload documentation](https://central.sonatype.org/publish/publish-portal-upload/) and [artifact requirements](https://central.sonatype.org/publish/requirements/).
