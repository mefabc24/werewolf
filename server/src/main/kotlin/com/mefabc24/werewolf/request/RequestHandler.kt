package com.mefabc24.werewolf.request

import com.mefabc24.werewolf.network.*

class RequestHandler {

    fun handle(playerId: Int, request: Request): RequestResult =
        when (request) {
            is PlaceholderRequest -> RequestResult(
                response = PlaceholderResponse,
                event = PlaceholderEvent
            )
            is MessageRequest -> RequestResult(
                event = MessageEvent(playerId, request.message)
            )
        }
}

