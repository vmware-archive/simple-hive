package io.pivotal.simplehive

import io.pivotal.simplehive.config.HiveConfig
import io.pivotal.simplehive.config.ServiceConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class SimpleHiveApplication : ApplicationRunner {

    @Autowired
    private lateinit var serviceConfig: ServiceConfig

    override fun run(args: ApplicationArguments) {

        val hiveServerContainer = Container(HiveConfig(HiveFileSystem(), serviceConfig))

        hiveServerContainer.init()

        hiveServerContainer.start()
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(SimpleHiveApplication::class.java, *args)
}
