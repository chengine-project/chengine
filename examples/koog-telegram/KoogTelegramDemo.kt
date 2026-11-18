package example.koog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(proxyBeanMethods = false)
class KoogTelegramDemo

fun main(args: Array<String>) {
    runApplication<KoogTelegramDemo>(*args)
}
