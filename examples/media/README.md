# Photos, documents, video, and audio

Copy `MediaHandler.kt` into a Spring Boot application using the Telegram starter and supply your own `photo.jpg`, `document.pdf`, `video.mp4`, and `audio.mp3`. The handler's chat ID is filled from the incoming update unless an explicit `chatId` is supplied.

All four builders accept a Telegram `file_id` or URL with `photo { name = "..." }`, `document { name = "..." }`, `video { name = "..." }`, or `audio { name = "..." }`, a `File` with `file = File("...")`, or an `InputStream` with `mediaStream = stream; name = "filename.ext"`. They also accept a direct Telegram `InputFile` through the `photo`, `document`, `video`, or `audio` property. Do not reuse a consumed stream for repeated sends.
