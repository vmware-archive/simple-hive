package io.pivotal.broker

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class SimpleHiveBrokerApplication

fun main(args: Array<String>) {
    SpringApplication.run(SimpleHiveBrokerApplication::class.java, *args)
}
