package io.chengine.autoconfigure

import io.chengine.Chengine
import io.chengine.ChengineConfig
import io.chengine.connector.AbstractBot
import io.chengine.handler.Handler
import io.chengine.session.SessionStore
import org.springframework.beans.factory.ObjectProvider
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class ChengineAutoConfiguration : ApplicationContextAware {

    private lateinit var applicationContext: ApplicationContext

    @Bean
    open fun config(sessionStores: ObjectProvider<SessionStore>): ChengineConfig {
        val handlers = applicationContext.getAllBeansAnnotatedBy<Handler>()
        val bots = applicationContext.getAllBeansOfType<AbstractBot>()
        return ChengineConfig(handlers, bots, sessionStores.ifAvailable)
    }

    @Bean
    open fun chengine(config: ChengineConfig): Chengine = Chengine(config)

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }
}