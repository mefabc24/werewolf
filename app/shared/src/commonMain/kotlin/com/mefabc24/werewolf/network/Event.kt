package com.mefabc24.werewolf.network

import com.mefabc24.werewolf.game.ClientGameState
import com.mefabc24.werewolf.game.DayPhase
import com.mefabc24.werewolf.game.DeathCause
import com.mefabc24.werewolf.game.NightPhase
import com.mefabc24.werewolf.player.role.Role
import com.mefabc24.werewolf.player.role.Team
import kotlinx.serialization.Serializable

@Serializable
sealed interface Event

// Lobby

@Serializable
data class PlayerJoinedEvent(
    val playerId: Int,
    val playerName: String
) : Event

@Serializable
data class PlayerLeftEvent(
    val playerId: Int,
    val playerName: String
) : Event

@Serializable
data class MessageEvent(
    val playerId: Int,
    val message: String
) : Event

// Game start and round beginning
@Serializable
data class GameStartedEvent(val gameState: ClientGameState) : Event

@Serializable
data class RoundStartedEvent(val round: Int) : Event

@Serializable
data class RoleRevealEvent(
    val playerId: Int,
    val role: Role
) : Event

@Serializable
data object NightStartedEvent : Event

@Serializable
data class NightPhaseStartedEvent(val phase: NightPhase) : Event

@Serializable
data object WerewolfTurnEvent : Event

@Serializable
data class WitchTurnEvent(
    val attackedPlayerIds: Set<Int>
) : Event

@Serializable
data object SeerTurnEvent : Event

@Serializable
data object HunterTurnEvent : Event

@Serializable
data class NightEndedEvent(val killedPlayerIds: Set<Int>) : Event

// Day phase and voting
@Serializable
data object DayStartedEvent : Event

@Serializable
data class DayPhaseStartedEvent(val phase: DayPhase) : Event

@Serializable
data class PlayerDiedEvent(
    val playerId: Int,
    val cause: DeathCause
) : Event

@Serializable
data class PlayerVotedEvent(val playerId: Int) : Event

@Serializable
data class VotingFinishedEvent(val killedPlayerId: Int?) : Event

// Game end
@Serializable
data class GameWonEvent(val winningTeam: Team) : Event

@Serializable
data object GameEndedEvent : Event
