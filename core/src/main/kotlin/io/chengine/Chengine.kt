package io.chengine

import io.chengine.connector.Factory
import io.chengine.handler.HandlerRegistrar
import io.chengine.message.DefaultMessageProcessor
import org.apache.logging.log4j.kotlin.logger
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import java.util.*

open class Chengine(config: ChengineConfig) {

    @ComponentScan(
        basePackages = [
            "io.chengine.annotation",
            "io.chengine.command",
            "io.chengine.common",
            "io.chengine.connector",
            "io.chengine.handler",
            "io.chengine.interceptor",
            "io.chengine.message",
            "io.chengine.method",
            "io.chengine.pipeline",
            "io.chengine.session",
        ],
        excludeFilters = [ComponentScan.Filter(Chengine::class, type = FilterType.ASSIGNABLE_TYPE)]
    )
    private class ComponentScanClass

    private val logger = logger()
    private var context: AnnotationConfigApplicationContext
    private var properties = Properties()

    init {
        loadProperties()
        printBanner()
        logger.info { "Chengine initializing started..." }
        context = AnnotationConfigApplicationContext(ComponentScanClass::class.java)
        config.bots.forEach { bot ->
            context
                .getBean(DefaultMessageProcessor::class.java)
                .let { bot.setMessageProcessor(it) }
            context
                .getBeansOfType(Factory::class.java)
                .values
                .forEach { it.put(bot) }
        }
        config.handlers?.forEach { register(it) }
        logger.info { "Chengine initialized!" }
    }

    fun register(handler: Any) {
        context.getBean(HandlerRegistrar::class.java).register(handler)
    }

    fun register(handlers: List<Any>) {
        handlers.forEach { register(it) }
    }

    private fun loadProperties() {
        javaClass.classLoader.getResourceAsStream("chengine.properties").use {
            properties.load(it)
        }
    }

    private fun printBanner() {
        if (properties["banner.enabled"] == "true") {
            logger.info {
                """
                
                
                 _______ _     _ _______ __   _  ______ _____ __   _ _______
                 |       |_____| |______ | \  | |  ____   |   | \  | |______
                 |_____  |     | |______ |  \_| |_____| __|__ |  \_| |______
                 Chengine Framework
                 ${properties.getProperty("version")}    
                                                  
            """.trimIndent()
            }
        }
    }
}