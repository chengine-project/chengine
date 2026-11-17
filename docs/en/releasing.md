---
layout: default
lang: en
title: Release preparation
section: DEVELOPMENT
permalink: /en/releasing/
lead: Check the build and local publication of libraries before release.
---

CI builds and tests on JDK 17 and 21. Session integration tests use a separate PostgreSQL test database. CI then publishes artifacts to an isolated local Maven repository and runs a separate consumer using both Gradle metadata and Maven POM.

To verify publication locally without changing the main Maven repository:

```bash
RELEASE_TEST_REPO="$(mktemp -d)"
./gradlew clean build publishToMavenLocal -Dmaven.repo.local="$RELEASE_TEST_REPO"
CHENGINE_LOCAL_MAVEN="$RELEASE_TEST_REPO" ./gradlew -p verification/consumer clean run
CHENGINE_LOCAL_MAVEN="$RELEASE_TEST_REPO" CHENGINE_POM_ONLY=true ./gradlew -p verification/consumer clean run
```

For PostgreSQL integration tests, set `CHENGINE_TEST_POSTGRES_URL`, `CHENGINE_TEST_POSTGRES_USER`, and `CHENGINE_TEST_POSTGRES_PASSWORD` before building. Tests delete rows from `chengine_sessions`, so use a dedicated test database. Without the URL these tests are skipped.

Public publication is not yet configured. Before the first release, choose a Maven repository, confirm ownership of the `io.github.chengine-project` coordinates, configure artifact signing and credentials, and check the repository's metadata and documentation requirements. The version is defined in the root `build.gradle.kts`; the MIT license is included in the JAR and declared in the POM.

For local signed-bundle preparation and verification without uploading, see the [release instructions](https://github.com/chengine-project/chengine/blob/main/verification/RELEASE.md).
