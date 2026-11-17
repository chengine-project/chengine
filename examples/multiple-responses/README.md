# Multiple responses

A Telegram handler may return a list of Telegram API methods. They are prepared before sending, then executed synchronously in list order. Each method gets the same automatic chat ID handling as an individual response; an explicit chat ID is preserved.

```kotlin
@HandleCommand("/hello")
fun hello() = listOf(
    sendTelegramMessage { text = "Первое сообщение" },
    sendTelegramMessage { text = "Второе сообщение" }
)
```

An empty list sends nothing. Nulls, nested lists, and non-Telegram values are rejected before any response is sent. If Telegram rejects a send, later responses are not sent; earlier sends cannot be rolled back and are not retried automatically. Use a list if order matters.
