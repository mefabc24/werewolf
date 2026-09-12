package com.mefabc24.werewolf.request

import com.mefabc24.werewolf.game.GameController
import com.mefabc24.werewolf.game.actions.*
import com.mefabc24.werewolf.lobby.LobbyController
import com.mefabc24.werewolf.network.*

class RequestHandler(
    private val gameController: GameController,
    private val lobbyController: LobbyController
) {

    suspend fun handle(playerId: Int, request: Request): RequestResult =
        when (request) {
            is MessageRequest -> RequestResult(
                event = MessageEvent(playerId, request.message)
            )

            is StartGameRequest -> {
                if (!lobbyController.isHost(playerId)) {
                    RequestResult(
                        response = ErrorResponse("Only the host can start the game.")
                    )
                } else {
                    gameController.start(
                        players = lobbyController.getPlayers(),
                        settings = lobbyController.getSettings()
                    )

                    RequestResult(
                        response = GameStartedResponse
                    )
                }
            }

            is WerewolfActionRequest -> {
                val success =
                     gameController.submitAction(
                        playerId = playerId,
                        action = WerewolfAction(targetId = request.targetId)
                    )

                RequestResult(
                    response = if (success) {
                        ActionAcceptedResponse
                    } else {
                        ErrorResponse("Invalid werewolf action.")
                    }
                )
            }

            is WitchActionRequest -> {
                val success = gameController.submitAction(
                    playerId,
                    WitchAction(
                        healTargetId = request.healTargetId,
                        killTargetId = request.killTargetId
                    )
                )

                RequestResult(
                    response = if (success) {
                        ActionAcceptedResponse
                    } else {
                        ErrorResponse("Invalid witch action.")
                    }
                )
            }

            is SeerActionRequest -> {
                val success = gameController.submitAction(
                    playerId,
                    SeerAction(targetId = request.targetId)
                )

                RequestResult(
                    response = if (success) {
                        ActionAcceptedResponse
                    } else {
                        ErrorResponse("Invalid seer action.")
                    }
                )
            }

            is HunterActionRequest -> {
                val success = gameController.submitAction(
                    playerId,
                    HunterAction(targetId = request.targetId)
                )

                RequestResult(
                    response = if (success) {
                        ActionAcceptedResponse
                    } else {
                        ErrorResponse("Invalid hunter action.")
                    }
                )
            }

            is VoteRequest -> {
                val success = gameController.submitVote(
                    playerId,
                    request.targetId
                )

                RequestResult(
                    response = if (success) {
                        VoteAcceptedResponse
                    } else {
                        ErrorResponse("Invalid vote.")
                    }
                )
            }
        }
}

