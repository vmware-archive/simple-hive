/*
 * Copyright (C) 2016-Present Pivotal Software, Inc. All rights reserved.
 *
 * This program and the accompanying materials are made available under
 *
 * the terms of the under the Apache License, Version 2.0 (the "License”);
 *
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 *
 * limitations under the License.
 */

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
