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

package io.pivotal.simplehive

import org.assertj.core.api.Assertions.assertThat
import org.datanucleus.store.rdbms.datasource.DriverManagerDataSource
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
@TestPropertySource(properties = arrayOf(
        "simple.hive.service.admin.username=test-admin",
        "simple.hive.service.admin.password=test-admin-password"))
class SimpleHiveApplicationTest {
    companion object {
        val testDatabase = "test_db"
        val testTable = "$testDatabase.test_table"

        val driver = "org.apache.hive.jdbc.HiveDriver"
        val url = "jdbc:hive2://localhost:8080/default;transportMode=http;httpPath=simple-hive"

        val jdbcTemplate = JdbcTemplate(DriverManagerDataSource(
                driver,
                url,
                "test-admin",
                "test-admin-password",
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

    @Test(expected = Exception::class)
    fun `should forbid access if unauthenticated`() {

        val jdbcTemplate = JdbcTemplate(DriverManagerDataSource(
                driver,
                url,
                "unauthenticated-user",
                "wrong-password",
                null,
                null))
        jdbcTemplate.queryForObject("SELECT 1", Int::class.java)
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
