package io.chengine.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnableTelegramCommandDescription(val command: String = "/help")
