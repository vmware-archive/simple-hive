package io.pivotal.sampleclient

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.sql.DriverManager
import java.sql.Statement

@SpringBootApplication
open class SampleClientApplication

@RestController
open class Web
constructor() {

    @PostMapping
    fun post() {
        writeToHive()
    }

    private fun writeToHive() {
        execute { statement ->
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS pingpong (player String, wins Int)")
            statement.executeUpdate("INSERT INTO pingpong VALUES ('Mohammad', 10), ('Max', 6)")

        }

    }

    @GetMapping
    fun get(): MutableList<Pair<String, Int>> {
        return readFromHive()
    }

    private fun readFromHive(): MutableList<Pair<String, Int>> {
        val pingPongPlayers = mutableListOf<Pair<String, Int>>()

        execute { statement ->
            val resultSet = statement.executeQuery("SELECT * from pingpong")
            while (resultSet.next()) {
                pingPongPlayers.add(Pair(resultSet.getString("player"), resultSet.getInt("wins")))
            }
        }
        return pingPongPlayers
    }

    private fun execute(action: (Statement) -> Unit) {
        val connection = DriverManager.getConnection(databaseUri())
        val statement = connection.createStatement()

        try {
            action(statement)
        } finally {
            statement.close()
            connection.close()
        }
    }

    private fun databaseUri(): String {
        val env = System.getenv("VCAP_SERVICES") ?: throw IllegalStateException("...")
        val begin = env.indexOf("jdbc:")
        val end = env.indexOf("\"}", begin)

        return env.substring(begin, end)
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(SampleClientApplication::class.java, *args)
}
