package com.mefabc24.werewolf.request

import com.mefabc24.werewolf.game.GameController
import com.mefabc24.werewolf.lobby.LobbyController
import com.mefabc24.werewolf.network.*

class RequestHandler(
    private val gameController: GameController,
    private val lobbyController: LobbyController
) {

    fun handle(playerId: Int, request: Request): RequestResult =
        when (request) {
            is PlaceholderRequest -> RequestResult(
                response = PlaceholderResponse,
                event = PlaceholderEvent
            )
            is MessageRequest -> RequestResult(
                event = MessageEvent(playerId, request.message)
            )
            is StartGameRequest -> {
                if (lobbyController.isHost(playerId)) {
                    RequestResult(
                        response = GameStartedResponse,
                        event = GameStartedEvent
                    )
                } else {
                    RequestResult(
                        response = ErrorResponse("Only the host can start the game.")
                    )
                }
            }
        }
}

