# Photos and documents

Copy `MediaHandler.kt` into a Spring Boot application using the Telegram starter and supply your own `photo.jpg` and `document.pdf`. The handler's chat ID is filled from the incoming update unless an explicit `chatId` is supplied.

Both builders accept a Telegram `file_id` or URL with `photo { name = "..." }` / `document { name = "..." }`, a `File` with `file = File("...")`, or an `InputStream` with `mediaStream = stream; name = "filename.ext"`. They also accept a direct Telegram `InputFile` through the `photo` or `document` property. Do not reuse a consumed stream for repeated sends.
