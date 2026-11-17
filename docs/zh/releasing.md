---
layout: default
lang: zh
title: 发布准备
section: 开发
permalink: /zh/releasing/
lead: 发布前检查库的构建与本地发布。
---

CI 使用 JDK 17 和 21 构建项目并运行测试。会话集成测试使用独立的 PostgreSQL 测试数据库。随后 CI 将构件发布到隔离的本地 Maven 仓库，并分别通过 Gradle 元数据和 Maven POM 运行独立的消费项目。

要在不修改主 Maven 仓库的情况下进行本地发布检查：

```bash
RELEASE_TEST_REPO="$(mktemp -d)"
./gradlew clean build publishToMavenLocal -Dmaven.repo.local="$RELEASE_TEST_REPO"
CHENGINE_LOCAL_MAVEN="$RELEASE_TEST_REPO" ./gradlew -p verification/consumer clean run
CHENGINE_LOCAL_MAVEN="$RELEASE_TEST_REPO" CHENGINE_POM_ONLY=true ./gradlew -p verification/consumer clean run
```

要运行 PostgreSQL 集成测试，请在构建前设置 `CHENGINE_TEST_POSTGRES_URL`、`CHENGINE_TEST_POSTGRES_USER` 和 `CHENGINE_TEST_POSTGRES_PASSWORD`。测试会删除 `chengine_sessions` 表中的行，因此请使用独立的测试数据库。未设置 URL 时会跳过这些测试。

公开发布尚未配置。首次发布前，需要选择 Maven 仓库、确认对 `io.github.chengine-project` 坐标的权限、配置构件签名和凭证，并检查所选仓库对元数据和文档的要求。版本由根目录的 `build.gradle.kts` 指定；MIT 许可证包含在 JAR 中，并在 POM 中声明。
