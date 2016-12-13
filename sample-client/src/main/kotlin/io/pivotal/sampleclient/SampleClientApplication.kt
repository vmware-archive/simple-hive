package io.pivotal.sampleclient

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


@SpringBootApplication
open class SampleClientApplication

fun main(args: Array<String>) {
    SpringApplication.run(SampleClientApplication::class.java, *args)
}

@RestController
open class Web
constructor(private val jdbcTemplate: JdbcTemplate) {

    init {
        jdbcTemplate.update("CREATE TABLE IF NOT EXISTS pingpong (player String)")
    }

    @PostMapping("/{player}")
    fun post(@PathVariable player: String) {
        jdbcTemplate.update("INSERT INTO pingpong VALUES (?)", player)
    }

    @GetMapping("/{player}")
    fun get(@PathVariable player: String): Int {
        return jdbcTemplate.queryForObject(
                "SELECT count(*) FROM pingpong WHERE player = ?",
                arrayOf(player),
                Int::class.java)
    }
}
