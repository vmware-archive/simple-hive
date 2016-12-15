package io.pivotal.simplehive

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "simple.hive.service.admin")
class AdminConfig {
    lateinit var username: String
    lateinit var password: String
}