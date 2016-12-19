package io.pivotal.cloud.hive

import org.apache.hive.jdbc.HiveDriver
import java.sql.Connection
import java.sql.Driver
import java.sql.DriverManager
import java.sql.SQLException
import java.util.*

/**
 * Since Hive jdbc driver {@code org.apache.hive.jdbc.HiveDriver} is not able to extract the username and password from jdbc URL,
 * this class acts as adapter to extract those information and provide them explicitly.
 */
class HiveDriverAdapter : Driver by driver {
    companion object {
        init {
            try {
                DriverManager.registerDriver(HiveDriverAdapter())
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }

        val driver: HiveDriver = HiveDriver()
    }

    override fun connect(url: String, info: Properties): Connection {

        val connectionInfo = ConnectionAdapter().adapt(url)

        info.setProperty("user", connectionInfo.username)
        info.setProperty("password", connectionInfo.password)

        return driver.connect(connectionInfo.uri, info)
    }
}

class ConnectionAdapter {
    fun adapt(url: String): ConnectionInfo {

        val startOfQuery = url.indexOf('?')
        if (startOfQuery == -1) return ConnectionInfo(url, "", "")

        val queryParams = url.substring(startOfQuery + 1).split('&').map {
            val (name, value) = it.split("=")
            Pair(name, value)
        }

        val user = queryParams.find { it.first == "user" }?.second ?: ""
        val password = queryParams.find { it.first == "password" }?.second ?: ""

        val newQueryParams = queryParams
                .filter { it.first !in listOf("user", "password") }
                .map { "${it.first}=${it.second}" }
                .joinToString("&")

        val urlBase = url.substring(0, startOfQuery)

        val newUrl = if (newQueryParams.isBlank()) urlBase else "$urlBase?$newQueryParams"

        return ConnectionInfo(newUrl, user, password)
    }
}

data class ConnectionInfo(val uri: String,
                          val username: String,
                          val password: String)
