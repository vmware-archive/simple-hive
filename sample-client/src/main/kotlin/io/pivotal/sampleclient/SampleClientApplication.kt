package io.pivotal.sampleclient

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import javax.sql.DataSource

@SpringBootApplication
open class SampleClientApplication

fun main(args: Array<String>) {
    SpringApplication.run(SampleClientApplication::class.java, *args)
}

@Configuration
@Profile("!cloud")
open class LocalConfig {

    @Bean
    open fun dataSource(): DataSource = DataSourceBuilder.create().build()
}