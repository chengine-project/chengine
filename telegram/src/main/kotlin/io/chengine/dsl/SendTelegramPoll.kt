package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.polls.SendPoll
import org.telegram.telegrambots.meta.api.objects.polls.input.InputPollOption

fun sendTelegramPoll(block: SendTelegramPollBuilder.() -> Unit): SendPoll =
    SendTelegramPollBuilder().apply(block)._build()

enum class TelegramPollType { REGULAR, QUIZ }

class SendTelegramPollBuilder(
    override var inlineKeyboard: TelegramInlineKeyboard? = null
) : InlineKeyboardSupporter {
    var chatId: String? = null
    var question: String? = null
    var options: List<String> = emptyList()
    var isAnonymous: Boolean? = null
    var type: TelegramPollType = TelegramPollType.REGULAR
    var allowMultipleAnswers: Boolean? = null
    /** Zero-based correct answer indexes, in strictly increasing order. */
    var correctOptionIds: List<Int>? = null
    var explanation: String? = null
    var explanationParseMode: ParseMode? = null
    var openPeriod: Int? = null
    var closeDate: Int? = null
    var disableNotification: Boolean? = null
    var replyToMessageId: Int? = null
    var allowSendingWithoutReply: Boolean? = null

    fun option(text: String) { options = options + text }

    fun _build(): SendPoll {
        val value = requireNotNull(question) { "Poll question is required" }
        require(value.isNotBlank() && value.codePointCount(0, value.length) <= 300) { "Poll question must contain 1–300 characters" }
        require(options.size in 1..12) { "A poll must have 1–12 options" }
        require(options.all { it.isNotBlank() && it.codePointCount(0, it.length) <= 100 }) { "Each option must contain 1–100 characters" }
        if (type == TelegramPollType.QUIZ) {
            val correct = requireNotNull(correctOptionIds) { "Quiz correctOptionIds are required" }
            require(correct.isNotEmpty() && correct.all { it in options.indices } && correct.zipWithNext().all { (a, b) -> a < b }) {
                "Quiz correctOptionIds must be valid, unique, increasing zero-based indexes"
            }
        } else {
            require(correctOptionIds == null) { "correctOptionIds are only supported for quizzes" }
            require(explanation == null && explanationParseMode == null) { "An explanation is only supported for quizzes" }
        }
        require(openPeriod == null || openPeriod in 5..2_628_000) { "openPeriod must be between 5 and 2628000 seconds" }
        require(openPeriod == null || closeDate == null) { "Choose either openPeriod or closeDate" }
        require(explanation == null || explanation!!.codePointCount(0, explanation!!.length) <= 200) { "Explanation must contain at most 200 characters" }
        return SendPoll.builder().chatId(chatId ?: "").question(value)
            .options(options.map { InputPollOption(it) }).build().apply {
                isAnonymous = this@SendTelegramPollBuilder.isAnonymous
                type = this@SendTelegramPollBuilder.type.name.lowercase(java.util.Locale.ROOT)
                allowMultipleAnswers = this@SendTelegramPollBuilder.allowMultipleAnswers
                correctOptionIds = this@SendTelegramPollBuilder.correctOptionIds
                explanation = this@SendTelegramPollBuilder.explanation
                explanationParseMode = when (this@SendTelegramPollBuilder.explanationParseMode) {
                    ParseMode.MARKDOWN -> "Markdown"
                    ParseMode.MARKDOWN_V2 -> "MarkdownV2"
                    ParseMode.HTML -> "HTML"
                    null -> null
                }
                openPeriod = this@SendTelegramPollBuilder.openPeriod
                closeDate = this@SendTelegramPollBuilder.closeDate
                disableNotification = this@SendTelegramPollBuilder.disableNotification
                replyToMessageId = this@SendTelegramPollBuilder.replyToMessageId
                allowSendingWithoutReply = this@SendTelegramPollBuilder.allowSendingWithoutReply
                replyMarkup = this@SendTelegramPollBuilder.inlineKeyboard.toReplyMarkup()
            }
    }
}
