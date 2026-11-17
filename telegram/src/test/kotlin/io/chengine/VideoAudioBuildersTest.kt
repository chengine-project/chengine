package io.chengine

import io.chengine.dsl.sendTelegramAudio
import io.chengine.dsl.sendTelegramVideo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.ByteArrayInputStream
import java.nio.file.Files
import java.nio.file.Path

class VideoAudioBuildersTest {
    @TempDir lateinit var dir: Path

    @Test fun `video and audio preserve file ids files and media options`() {
        val path = Files.writeString(dir.resolve("sample.bin"), "test").toFile()
        val videoId = sendTelegramVideo { video { name = "video-id" }; duration = 12; width = 640; height = 360; supportsStreaming = true }
        val audioId = sendTelegramAudio { audio { name = "audio-id" }; duration = 25; title = "title"; performer = "artist" }
        assertFalse(videoId.video.isNew); assertEquals("video-id", videoId.video.attachName)
        assertEquals(12, videoId.duration); assertEquals(640, videoId.width); assertEquals(360, videoId.height); assertTrue(videoId.supportsStreaming)
        assertFalse(audioId.audio.isNew); assertEquals("audio-id", audioId.audio.attachName)
        assertEquals(25, audioId.duration); assertEquals("title", audioId.title); assertEquals("artist", audioId.performer)
        assertEquals(path, sendTelegramVideo { video { file = path } }.video.newMediaFile)
        assertEquals(path, sendTelegramAudio { audio { file = path } }.audio.newMediaFile)
    }
    @Test fun `streams and missing media are handled consistently`() {
        val stream = ByteArrayInputStream(byteArrayOf(1, 2))
        assertSame(stream, sendTelegramVideo { video { name = "clip.mp4"; mediaStream = stream } }.video.newMediaStream)
        assertSame(stream, sendTelegramAudio { audio { name = "song.mp3"; mediaStream = stream } }.audio.newMediaStream)
        assertThrows(IllegalArgumentException::class.java) { sendTelegramVideo {} }
        assertThrows(IllegalArgumentException::class.java) { sendTelegramAudio {} }
        assertThrows(IllegalArgumentException::class.java) { sendTelegramAudio { audio { mediaStream = stream } } }
    }
}
