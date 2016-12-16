package io.pivotal.sampleclient.pingpong

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service

@Service
open class PingPongService(private val jdbcTemplate: JdbcTemplate) {

    init {
        jdbcTemplate.update("CREATE TABLE IF NOT EXISTS ping_pong (player String)")
    }

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
