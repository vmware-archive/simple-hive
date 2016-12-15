package io.pivotal.simplehive

import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

@Component
class SpringContextBridge
constructor(val adminConfig: AdminConfig) : ApplicationContextAware {

    companion object {
        private var applicationContext: ApplicationContext? = null

        fun springContextBridge(): SpringContextBridge {
            return applicationContext!!.getBean(SpringContextBridge::class.java)
        }
    }

    @Throws(BeansException::class)
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        SpringContextBridge.applicationContext = applicationContext
    }

}