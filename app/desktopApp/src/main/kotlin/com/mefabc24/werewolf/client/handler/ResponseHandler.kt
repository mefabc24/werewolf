package com.mefabc24.werewolf.client.handler

import com.mefabc24.werewolf.client.ClientState
import com.mefabc24.werewolf.network.*

class ResponseHandler(
    private val clientState: ClientState
) {
    fun handle(response: Response) {
        when (response) {
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