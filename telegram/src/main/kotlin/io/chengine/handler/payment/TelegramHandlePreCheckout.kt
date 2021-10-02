package io.chengine.handler.payment

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class TelegramHandlePreCheckout(val identifier: String = "")
