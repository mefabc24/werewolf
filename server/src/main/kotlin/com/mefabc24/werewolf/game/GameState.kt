package com.mefabc24.werewolf.game

import com.mefabc24.werewolf.game.role.SeerState
import com.mefabc24.werewolf.game.role.WitchState
import com.mefabc24.werewolf.player.Player
import com.mefabc24.werewolf.settings.GameSettings

data class GameState(
    val players: MutableList<Player> = mutableListOf(),
    var gamePhase: GamePhase = GamePhase.LOBBY,
    var nightPhase: NightPhase? = null,
    var dayPhase: DayPhase? = null,
    val votes: MutableSet<Vote> = mutableSetOf(),
    var round: Int = 0,
    var settings: GameSettings = GameSettings(),

    // role states
    var witchStates: MutableMap<Int, WitchState> = mutableMapOf(),
    var seerStates: MutableMap<Int, SeerState> = mutableMapOf(),
)
