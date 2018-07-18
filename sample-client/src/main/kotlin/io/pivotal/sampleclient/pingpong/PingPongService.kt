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

package io.pivotal.sampleclient.pingpong

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
class PingPongService(private val jdbcTemplate: JdbcTemplate) {

    fun recordWinForPlayer(player: String) {
        jdbcTemplate.update("INSERT INTO ping_pong VALUES (?)", player)
    }

    fun getWinsForPlayer(player: String): Int {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(player) FROM ping_pong WHERE player = ?",
                arrayOf(player),
                Int::class.java)
    }

}
