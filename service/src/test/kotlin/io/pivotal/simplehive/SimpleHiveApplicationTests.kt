package io.pivotal.simplehive

import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.sql.Connection
import java.sql.DriverManager

@RunWith(SpringRunner::class)
@SpringBootTest
class SimpleHiveApplicationTests {
    companion object {
        val testDatabase = "test_db"
        val testTable = "$testDatabase.test_table"
        val simpleHiveHost = "localhost:8080"
    }

    lateinit var connection: Connection

    @Before
    fun setup() {
        connection = DriverManager.getConnection("jdbc:hive2://$simpleHiveHost/default;transportMode=http;httpPath=simple-hive")
    }

    @After
    fun cleanup() {
        connection.close()
    }

    @Test
    fun `should provide INSERT and SELECT operations`() {

        givenTestDatabaseExists()

        val records = listOf(Pair("two", 2), Pair("five", 5))

        whenDataIsInserted(records)

        thenDataCanBeRetrieved(records)
    }

    private fun thenDataCanBeRetrieved(records: List<Pair<String, Int>>) {
        val resultSet = connection.createStatement().executeQuery("select * from $testTable order by value")

        records.forEach {
            assertThat(resultSet.next()).isTrue()
            assertThat(resultSet.getString("key")).isEqualTo(it.first)
            assertThat(resultSet.getInt("value")).isEqualTo(it.second)
        }

        assertThat(resultSet.next()).isFalse()
    }

    private fun whenDataIsInserted(records: List<Pair<String, Int>>) {
        records.forEach {
            connection.createStatement().executeUpdate("insert into $testTable values ('${it.first}', ${it.second})")
        }
    }

    private fun givenTestDatabaseExists() {
        connection.createStatement().executeUpdate("DROP DATABASE IF EXISTS $testDatabase CASCADE")
        connection.createStatement().executeUpdate("CREATE DATABASE $testDatabase")
        connection.createStatement().executeUpdate("CREATE TABLE $testTable (key STRING, value INT)")
    }
}
