package io.chengine.command

/**
 * @see CommandDescription
 * @author Ilya Mikheev
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CommandDescriptionScope(val value: String) {
    companion object {
        const val DEFAULT = "default"
        const val ALL_CHAT_ADMINISTRATORS = "all_chat_administrators"
        const val ALL_GROUP_CHATS = "all_group_chats"
        const val ALL_PRIVATE_CHATS = "all_private_chats"
    }
}
