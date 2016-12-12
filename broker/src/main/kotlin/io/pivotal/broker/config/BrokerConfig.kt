package io.pivotal.broker.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "simple.hive")
class BrokerConfig {
    lateinit var serviceHost: String
}