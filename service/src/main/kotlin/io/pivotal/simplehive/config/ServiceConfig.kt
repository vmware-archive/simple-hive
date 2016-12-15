package io.pivotal.simplehive.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "simple.hive.service")
class ServiceConfig {
    lateinit var httpPath: String
    lateinit var port: String
}