package com.mefabc24.werewolf.client.handler

import com.mefabc24.werewolf.client.ClientState
import com.mefabc24.werewolf.network.ActionAcceptedResponse
import com.mefabc24.werewolf.network.ConnectedResponse
import com.mefabc24.werewolf.network.ErrorResponse
import com.mefabc24.werewolf.network.GameStartedResponse
import com.mefabc24.werewolf.network.PlaceholderResponse
import com.mefabc24.werewolf.network.Response
import com.mefabc24.werewolf.network.VoteAcceptedResponse

class ResponseHandler(
    private val clientState: ClientState
) {
    fun handle(response: Response) {
        when (response) {
            is PlaceholderResponse -> println("Response received: $response")

            is ConnectedResponse -> {
                clientState.id = response.playerId

                clientState.gameState = clientState.gameState.copy(
                    players = response.players
                )

                val playerName = clientState.selfPlayer?.playerName

                println("Connected as $playerName (ID: ${response.playerId})")
            }

            is GameStartedResponse -> println("Game started successfully")

            is ErrorResponse -> println("Error: ${response.message}")

            is ActionAcceptedResponse -> println("Action was successful")

            is VoteAcceptedResponse -> println("Vote was successful")
        }
    }
}