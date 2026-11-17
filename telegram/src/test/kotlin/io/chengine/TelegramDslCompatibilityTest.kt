package io.chengine

import io.chengine.dsl.deleteTelegramMessage
import io.chengine.dsl.sendTelegramContact
import io.chengine.dsl.sendTelegramDice
import io.chengine.dsl.sendTelegramLocation
import io.chengine.dsl.sendTelegramPhoto
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.objects.InputFile

class TelegramDslCompatibilityTest {
    @Test fun `builders supply required fields and preserve supplied data`() {
        val contact = sendTelegramContact { firstName = "A"; phoneNumber = "+123" }
        assertEquals("A", contact.firstName)
        val location = sendTelegramLocation { latitude = 55.0; longitude = 37.0; chatId = "42" }
        assertEquals("42", location.chatId)
        assertEquals(55.0, location.latitude)
        val dice = sendTelegramDice { emoji = "🎲" }
        assertEquals("🎲", dice.emoji)
        val photo = sendTelegramPhoto { photo = InputFile("file-id") }
        assertNotNull(photo.photo)
        val deletion = deleteTelegramMessage { messageId = 12 }
        assertEquals(12, deletion.messageId)
    }
}
