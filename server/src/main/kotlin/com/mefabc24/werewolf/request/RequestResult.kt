package com.mefabc24.werewolf.request

import com.mefabc24.werewolf.network.Event
import com.mefabc24.werewolf.network.Response

data class RequestResult(
    val response: Response? = null,
    val event: Event? = null
)
