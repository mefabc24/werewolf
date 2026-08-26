package com.mefabc24.werewolf.client.handler

import com.mefabc24.werewolf.client.ClientState
import com.mefabc24.werewolf.network.ConnectedResponse
import com.mefabc24.werewolf.network.ErrorResponse
import com.mefabc24.werewolf.network.GameStartedResponse
import com.mefabc24.werewolf.network.PlaceholderResponse
import com.mefabc24.werewolf.network.Response

class ResponseHandler(
    private val state: ClientState
) {
    fun handle(response: Response) {
        when (response) {
            is PlaceholderResponse -> println("Response received: $response")

            is ConnectedResponse -> {
                state.playerId = response.playerId

                state.players.clear()
                state.players.addAll(response.players)

                val playerName = state.players
                    .find { it.playerId == state.playerId }
                    ?.playerName

                println("Connected as $playerName (ID: ${response.playerId})")
            }

            is GameStartedResponse -> println("Game started successfully")

            is ErrorResponse -> println("Error: ${response.message}")
        }
    }
}