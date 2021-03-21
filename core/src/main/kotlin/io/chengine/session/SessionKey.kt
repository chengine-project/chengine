package io.chengine.session

data class SessionKey(
    val userId: String,
    val chatId: String,
    val botApiIdentifier: String
)
