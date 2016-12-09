package io.pivotal.simplehive

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class SimpleHiveApplication : ApplicationRunner {

    override fun run(args: ApplicationArguments) {

        val hiveServerContainer = Container(Config(HiveFileSystem()))

        hiveServerContainer.init()

        hiveServerContainer.start()
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(SimpleHiveApplication::class.java, *args)
}
