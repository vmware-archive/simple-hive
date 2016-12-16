package io.pivotal.sampleclient.pingpong

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
open class PingPongController(private val pingPongService: PingPongService) {

    @PostMapping("/{player}")
    fun post(@PathVariable player: String) {
        pingPongService.recordWinForPlayer(player)
    }

    @GetMapping("/{player}")
    fun get(@PathVariable player: String): Int {
        return pingPongService.getWinsForPlayer(player)
    }
}
