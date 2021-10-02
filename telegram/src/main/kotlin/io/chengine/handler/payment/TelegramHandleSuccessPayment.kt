package io.chengine.handler.payment

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class TelegramHandleSuccessPayment(val identifier: String = "")
