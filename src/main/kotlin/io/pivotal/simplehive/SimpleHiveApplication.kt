package io.pivotal.simplehive

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class SimpleHiveApplication

fun main(args: Array<String>) {
    SpringApplication.run(SimpleHiveApplication::class.java, *args)
}
