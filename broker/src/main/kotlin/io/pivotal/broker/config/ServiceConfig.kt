package io.pivotal.broker.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "simple.hive.service")
class ServiceConfig {
    lateinit var host: String
    lateinit var port: String
}