---
layout: default
lang: zh
title: 快速开始
section: 基础
permalink: /zh/start/
lead: 添加 Chengine、配置机器人并创建第一个处理器。
---

## 环境要求

- JDK 17 或更高版本。CI 使用 JDK 17 和 21 进行测试。
- Kotlin 和 Spring Boot；当前分支使用 Kotlin 2.2.21 和 Spring Boot 3.5.16。
- 通过 BotFather 创建的 Telegram 机器人及其令牌和用户名。

## 1. 获取依赖

Chengine 构件通过 Maven Central 发布。请查看 [starter 的可用版本](https://central.sonatype.com/artifact/io.github.chengine-project/spring-boot-starter-telegram)，并在以下示例中使用固定版本。新发布的版本可能不会立即可用。

如果版本尚不可用，或需要使用当前源代码，请在本地构建：

```bash
git clone https://github.com/chengine-project/chengine.git
cd chengine
./gradlew publishToMavenLocal
```

使用本地构建时，请在下方 `repositories` 中将 `mavenLocal()` 放在 `mavenCentral()` 之前。版本号以所选提交根目录的 `build.gradle.kts` 为准。

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
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("io.github.chengine-project:spring-boot-starter-telegram:1.0.0")
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


## 使用 Webhook 代替长轮询

默认使用轮询。使用 Webhook 时，添加依赖 `implementation("org.springframework.boot:spring-boot-starter-web")` 并配置应用：

```yaml
chengine:
  telegram:
    token: ${TELEGRAM_BOT_TOKEN}
    username: ${TELEGRAM_BOT_USERNAME}
    mode: webhook
    webhook:
      path: /telegram/webhook
      secret: ${TELEGRAM_WEBHOOK_SECRET}
```

通过服务器或反向代理以 HTTPS 提供 `https://example.com/telegram/webhook`。设置 `TELEGRAM_WEBHOOK_SECRET`（1–256 个 ASCII 字母、数字、`_` 或 `-`），启动应用后向 Telegram 注册 URL：

```bash
export TELEGRAM_WEBHOOK_URL=https://example.com/telegram/webhook
curl -fsS "https://api.telegram.org/bot${TELEGRAM_BOT_TOKEN}/setWebhook" \
  --data-urlencode "url=${TELEGRAM_WEBHOOK_URL}" \
  --data-urlencode "secret_token=${TELEGRAM_WEBHOOK_SECRET}"
```

如果使用 Spring Security，请允许此路径的 POST 请求并将其排除在 CSRF 检查之外：请求通过秘密请求头验证。Telegram 会重试失败的投递，处理程序应考虑重复消息。要恢复轮询，请通过 `deleteWebhook` 删除 Webhook。

[下一步：命令和处理器 →]({{ '/zh/handlers/' | relative_url }})
