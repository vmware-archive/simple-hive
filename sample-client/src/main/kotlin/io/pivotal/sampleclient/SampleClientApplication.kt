package io.pivotal.sampleclient

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class SampleClientApplication

fun main(args: Array<String>) {
    SpringApplication.run(SampleClientApplication::class.java, *args)
}