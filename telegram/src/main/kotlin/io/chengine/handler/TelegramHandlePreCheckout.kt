package io.chengine.handler

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class TelegramHandlePreCheckout(val identifier: String = "")
