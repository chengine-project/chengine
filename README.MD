# Chengine

Create chat bots easily!

## Quick Start With Spring Boot

### Telegram Yaml Configuration

You can configure telegram long pooling bot by adding following lines below into your `application.yml` config file:

```yaml
chengine:
  telegram:
    token: 1173254904:AAABBBCCCEEEDDDFFFGGGIIIE
    username: just_some_bot_username
```

### Command Handler

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
}

```



