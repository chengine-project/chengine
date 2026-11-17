package io.chengine

import io.chengine.dsl.sendTelegramDocument
import io.chengine.dsl.sendTelegramPhoto
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.nio.file.Path

class MediaBuildersTest {
    @TempDir lateinit var dir: Path

    @Test fun `photo and document accept file ids and local files`() {
        val path = Files.writeString(dir.resolve("example.txt"), "data").toFile()
        val photoId = sendTelegramPhoto { photo { name = "photo-id" } }.photo
        val documentId = sendTelegramDocument { document { name = "document-id" } }.document
        assertFalse(photoId.isNew); assertEquals("photo-id", photoId.attachName)
        assertFalse(documentId.isNew); assertEquals("document-id", documentId.attachName)
        val photo = sendTelegramPhoto { photo { file = path } }.photo
        val document = sendTelegramDocument { document { file = path; name = "report.txt" } }.document
        assertTrue(photo.isNew); assertEquals(path, photo.newMediaFile)
        assertTrue(document.isNew); assertEquals(path, document.newMediaFile)
        assertEquals("report.txt", document.mediaName)
    }
    @Test fun `stream needs a name and conflicting sources are rejected`() {
        val stream = ByteArrayInputStream(byteArrayOf(1, 2))
        val doc = sendTelegramDocument { document { mediaStream = stream; name = "test.bin" } }
        assertSame(stream, doc.document.newMediaStream)
        assertThrows(IllegalArgumentException::class.java) { sendTelegramPhoto { photo { mediaStream = stream } } }
        assertThrows(IllegalArgumentException::class.java) { sendTelegramDocument { document { name = " " } } }
        assertThrows(IllegalArgumentException::class.java) { sendTelegramDocument { document { file = dir.toFile(); mediaStream = stream } } }
        assertThrows(IllegalArgumentException::class.java) { sendTelegramDocument {} }
    }
}
