package io.pivotal.simplehive

import org.assertj.core.api.Assertions.assertThat
import org.datanucleus.store.rdbms.datasource.DriverManagerDataSource
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class SimpleHiveApplicationTests {
    companion object {
        val testDatabase = "test_db"
        val testTable = "$testDatabase.test_table"

        val driver = "org.apache.hive.jdbc.HiveDriver"
        val url = "jdbc:hive2://localhost:8080/default;transportMode=http;httpPath=simple-hive"

        val jdbcTemplate = JdbcTemplate(DriverManagerDataSource(
                driver,
                url,
                null,
                null,
                null,
                null))
    }

    @Test
    fun `should provide INSERT and SELECT operations`() {

        givenTestDatabaseExists()

        val records = listOf(Pair("two", 2), Pair("five", 5))

        whenDataIsInserted(records)

        thenDataCanBeRetrieved(records)
    }

    private fun thenDataCanBeRetrieved(records: List<Pair<String, Int>>) {
        val result = jdbcTemplate.query("SELECT * FROM $testTable ORDER BY VALUE") { rs, n ->

            Pair(rs.getString("key"), rs.getInt("value"))
        }

        assertThat(result).isEqualTo(records)
    }

    private fun whenDataIsInserted(records: List<Pair<String, Int>>) {
        records.forEach {
            jdbcTemplate.update("INSERT INTO $testTable VALUES ('${it.first}', ${it.second})")
        }
    }

    private fun givenTestDatabaseExists() {
        jdbcTemplate.update("DROP DATABASE IF EXISTS $testDatabase CASCADE")
        jdbcTemplate.update("CREATE DATABASE $testDatabase")
        jdbcTemplate.update("CREATE TABLE $testTable (key STRING, value INT)")
    }
}
