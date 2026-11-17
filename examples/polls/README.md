# Polls and quizzes

Copy `PollHandler.kt` into a Spring Boot application using the Telegram starter. Send `/poll` or `/quiz` to create a non-anonymous poll or quiz, then vote. `correctOptionIds` contains zero-based indexes, strictly increasing. To create an anonymous poll, set `isAnonymous = true` (or leave it unset: Telegram defaults to anonymous).

Telegram delivers individual `poll_answer` updates only for non-anonymous polls sent by the bot. Votes can be retracted, in which case `optionIds` may be empty, and a voter may be represented by `voterChat` rather than `user`. `poll` updates carry aggregate state for polls sent by the bot. Poll and poll-answer updates do not include the original chat ID, and no Chengine chat session is derived for them. To send a message to the original chat, persist a poll ID to chat ID mapping when creating the poll and provide the destination explicitly. The sample logs poll IDs and option indexes; replace this with your application logic as needed.
