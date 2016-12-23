package io.pivotal.simplehive

import io.pivotal.simplehive.config.AdminConfig
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

@Component
class SpringContextBridge
constructor(val adminConfig: AdminConfig) : ApplicationContextAware {

    companion object {
        private lateinit var applicationContext: ApplicationContext

        fun springContextBridge(): SpringContextBridge = applicationContext.getBean(SpringContextBridge::class.java)
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        SpringContextBridge.applicationContext = applicationContext
    }
}