---
layout: default
lang: zh
title: 快速开始
section: 基础
permalink: /zh/start/
lead: 在本地构建库，添加 starter，然后创建第一个处理器。
---

## 环境要求

- JDK 17 或更高版本。CI 使用 JDK 17 和 21 进行测试。
- Kotlin 和 Spring Boot；当前分支使用 Kotlin 2.2.21 和 Spring Boot 3.5.16。
- 通过 BotFather 创建的 Telegram 机器人及其令牌和用户名。

## 1. 构建库

以下方法从本地 Maven 仓库引入当前源代码，不依赖已发布的版本。

```bash
git clone https://github.com/chengine-project/chengine.git
cd chengine
./gradlew publishToMavenLocal
```

当前分支的构件版本为 `1.0.1-BETA`。如果使用其他提交，请检查根目录 `build.gradle.kts` 中的 `version`。

## 2. 添加 starter

在安装了 JDK 21 的独立 Spring Boot 项目的 `build.gradle.kts` 中添加：

```kotlin
plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    id("org.springframework.boot") version "3.5.16"
    id("io.spring.dependency-management") version "1.1.7"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("io.chengine:spring-boot-starter-telegram:1.0.1-BETA")
}

kotlin { jvmToolchain(21) }
```

## 3. 配置机器人

在 `src/main/resources/application.yml` 中：

```yaml
chengine:
  telegram:
    token: ${TELEGRAM_BOT_TOKEN}
    username: ${TELEGRAM_BOT_USERNAME}
```

运行前请设置环境变量。不要将令牌提交到 Git。

## 4. 添加处理器

```kotlin
import io.chengine.command.HandleCommand
import io.chengine.dsl.sendTelegramMessage
import io.chengine.handler.Handler
import org.springframework.stereotype.Component

@Handler
@Component
class HelloHandler {
    @HandleCommand("/hello")
    fun hello() = sendTelegramMessage {
        text = "你好！"
    }
}
```

在处理器所在包的父包中添加应用入口：

```kotlin
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
```

启动应用并向机器人发送 `/hello`。starter 使用长轮询。

[下一步：命令和处理器 →]({{ '/zh/handlers/' | relative_url }})
