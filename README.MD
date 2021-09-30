# Chengine

Create chat bots easily!

## Quick Start With Spring Boot

### Add Dependency

Gradle
```kotlin
dependencies {
    implementation("io.chengine:spring-boot-starter-telegram:0.1-ALPHA")
}
```

Maven
```xml
<dependencies>
    <!--...-->
    <depencency>
        <groupId>io.chengine</groupId>
        <artifactId>spring-boot-starter-telegram</artifactId>
        <version>0.1-ALPHA</version>
    </depencency>
</dependencies>
```
### Telegram Yaml Configuration

You can configure telegram long pooling bot by adding following lines below into your `application.yml` config file:

```yaml
chengine:
  telegram:
    token: 1173254904:AAABBBCCCEEEDDDFFFGGGIIIE
    username: just_some_bot_username
```

### Handler

```kotlin

import io.chengine.command.HandleCommand
import io.chengine.dsl.ParseMode
import io.chengine.dsl.sendTelegramMessage
import io.chengine.handler.Handler
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@Handler
@Component
class HelloHandler {

    @HandleCommand("/hi")
    fun hi(): SendMessage =
        sendTelegramMessage {
            text = "Hi!"
        }

    @HandleCommand("/hello")
    fun hello(update: Update): SendMessage =
        sendTelegramMessage {
            text = "*Hello user ${update.message.from.userName}!*"
            parseMode = ParseMode.MARKDOWN
        }
    
    // Or you may use @HandleText annotation to bind methods with human readable text
    
    @HandleText("❤️ hello")
    fun helloHuman() = sendTelegramMessage {
        text = "Hi Human!"
    }
}

```



## Samples

You can find chengine samples [here](https://github.com/mikheevshow/chengine-samples)

## How To Help

### Paste link into start message ❤️


You can help us become a little better known by pasting the link to our framework at the end of the start message, as in the example below.

```kotlin



@Handler
@Component
class StartHandler {
    
    @HandleCommand("/start")
    fun start() = sendTelegramMessage {
        text = """
            Some beautiful message about your bot! You may past our link at the end of */start* message
            
            Powered by [Chengine](https://github.com/IsaacOldton/chengine)
        """.trimIndent()
        
        parseMode = ParseMode.MARKDOWN
        disableWebPagePreview = true
    }
}

```

As a result, you will get something like this

<img width="849" alt="Снимок экрана 2021-09-30 в 04 28 29" src="https://user-images.githubusercontent.com/10999015/135370774-363b823b-fd22-401c-9742-b9bc5f6460b1.png">


