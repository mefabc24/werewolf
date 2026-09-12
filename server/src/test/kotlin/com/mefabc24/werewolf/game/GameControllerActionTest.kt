package com.mefabc24.werewolf.game

import com.mefabc24.werewolf.game.actions.SeerAction
import com.mefabc24.werewolf.game.actions.WerewolfAction
import com.mefabc24.werewolf.game.actions.WitchAction
import com.mefabc24.werewolf.network.Event
import com.mefabc24.werewolf.network.GameEndedEvent
import com.mefabc24.werewolf.network.GameWonEvent
import com.mefabc24.werewolf.network.NightEndedEvent
import com.mefabc24.werewolf.network.RoleRevealEvent
import com.mefabc24.werewolf.network.SeerTurnEvent
import com.mefabc24.werewolf.network.WerewolfTurnEvent
import com.mefabc24.werewolf.network.WitchTurnEvent
import com.mefabc24.werewolf.player.Player
import com.mefabc24.werewolf.player.role.OptionalRole
import com.mefabc24.werewolf.player.role.Seer
import com.mefabc24.werewolf.player.role.Team
import com.mefabc24.werewolf.player.role.Werewolf
import com.mefabc24.werewolf.player.role.Witch
import com.mefabc24.werewolf.settings.GameSettings
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class GameControllerActionTest {

    @Test
    fun werewolvesCannotAttackThemselvesTeammatesOrUnknownPlayers() = runBlocking {
        val state = GameState()
        val controller = GameController(state)
        var checkedValidation = false
        var attackedPlayerId: Int? = null

        val events = playGameUntilEnd(
            controller = controller,
            state = state,
            playerCount = 5,
            settings = fastSettings(werewolfAmount = 2),
            onEvent = { gameEvent ->
                if (gameEvent.event == WerewolfTurnEvent) {
                    val wolfIds = gameEvent.recipients.sorted()
                    val victim = state.players.first { it.role?.team == Team.VILLAGE }
                    attackedPlayerId = victim.id

                    assertEquals(2, wolfIds.size)
                    assertFalse(controller.submitAction(victim.id, WerewolfAction(wolfIds.first())))
                    wolfIds.forEach { wolfId ->
                        val teammateId = wolfIds.first { it != wolfId }
                        assertFalse(controller.submitAction(wolfId, WerewolfAction(wolfId)))
                        assertFalse(controller.submitAction(wolfId, WerewolfAction(teammateId)))
                        assertFalse(controller.submitAction(wolfId, WerewolfAction(999)))
                        assertTrue(controller.submitAction(wolfId, WerewolfAction(victim.id)))
                    }
                    checkedValidation = true
                }
            }
        )

        assertTrue(checkedValidation)
        assertTrue(events.contains(NightEndedEvent(setOf(assertNotNull(attackedPlayerId)))))
        assertTrue(events.contains(GameWonEvent(Team.WEREWOLVES)))
    }

    @Test
    fun seerRejectsSelfAndUnknownTargetsButRevealsAValidTarget() = runBlocking {
        val state = GameState()
        val controller = GameController(state)
        var seerId: Int? = null
        var wolfId: Int? = null

        val gameEvents = playGameEventsUntilEnd(
            controller = controller,
            state = state,
            playerCount = 3,
            settings = fastSettings(
                optionalRoles = mapOf(OptionalRole.SEER to 1)
            ),
            onEvent = { gameEvent ->
                when (gameEvent.event) {
                    WerewolfTurnEvent -> {
                        val actingWolfId = gameEvent.recipients.single()
                        wolfId = actingWolfId
                        val victim = state.players.single {
                            it.role?.team == Team.VILLAGE && it.role != Seer
                        }
                        assertTrue(controller.submitAction(actingWolfId, WerewolfAction(victim.id)))
                    }

                    SeerTurnEvent -> {
                        val actingSeerId = gameEvent.recipients.single()
                        seerId = actingSeerId
                        assertFalse(controller.submitAction(actingSeerId, SeerAction(actingSeerId)))
                        assertFalse(controller.submitAction(actingSeerId, SeerAction(999)))
                        assertTrue(controller.submitAction(actingSeerId, SeerAction(wolfId)))
                    }

                    else -> Unit
                }
            }
        )

        assertTrue(
            gameEvents.any { gameEvent ->
                gameEvent.recipients == setOf(seerId) &&
                        gameEvent.event == RoleRevealEvent(assertNotNull(wolfId), Werewolf)
            }
        )
        assertTrue(gameEvents.map { it.event }.contains(GameWonEvent(Team.WEREWOLVES)))
    }

    @Test
    fun witchCannotUseBothPotionsWhenTheSettingForbidsIt() = runBlocking {
        val state = GameState()
        val controller = GameController(state)
        var werewolfVictimId: Int? = null
        var poisonTargetId: Int? = null
        var checkedValidation = false

        val events = playGameUntilEnd(
            controller = controller,
            state = state,
            playerCount = 4,
            settings = fastSettings(
                canWitchUseBothPotions = false,
                optionalRoles = mapOf(OptionalRole.WITCH to 1)
            ),
            onEvent = { gameEvent ->
                when (gameEvent.event) {
                    WerewolfTurnEvent -> {
                        val wolfId = gameEvent.recipients.single()
                        val villagers = state.players.filter { it.role != Werewolf && it.role != Witch }
                        werewolfVictimId = villagers.first().id
                        poisonTargetId = villagers.last().id
                        assertTrue(controller.submitAction(wolfId, WerewolfAction(werewolfVictimId)))
                    }

                    is WitchTurnEvent -> {
                        val witchId = gameEvent.recipients.single()
                        assertFalse(
                            controller.submitAction(
                                witchId,
                                WitchAction(werewolfVictimId, poisonTargetId)
                            )
                        )
                        assertFalse(controller.submitAction(witchId, WitchAction(healTargetId = poisonTargetId)))
                        assertFalse(controller.submitAction(witchId, WitchAction(killTargetId = witchId)))
                        assertTrue(controller.submitAction(witchId, WitchAction(killTargetId = poisonTargetId)))
                        checkedValidation = true
                    }

                    else -> Unit
                }
            }
        )

        assertTrue(checkedValidation)
        assertTrue(
            events.contains(
                NightEndedEvent(setOf(assertNotNull(werewolfVictimId), assertNotNull(poisonTargetId)))
            )
        )
    }

    @Test
    fun witchCannotHealHerselfWhenTheSettingForbidsIt() = runBlocking {
        val state = GameState()
        val controller = GameController(state)
        var selfHealWasRejected = false

        val events = playGameUntilEnd(
            controller = controller,
            state = state,
            playerCount = 4,
            settings = fastSettings(
                canWitchHealSelf = false,
                optionalRoles = mapOf(OptionalRole.WITCH to 1)
            ),
            onEvent = { gameEvent ->
                when (gameEvent.event) {
                    WerewolfTurnEvent -> {
                        val wolfId = gameEvent.recipients.single()
                        val witchId = state.players.single { it.role == Witch }.id
                        assertTrue(controller.submitAction(wolfId, WerewolfAction(witchId)))
                    }

                    is WitchTurnEvent -> {
                        val witchId = gameEvent.recipients.single()
                        val poisonTarget = state.players.first {
                            it.role?.team == Team.VILLAGE && it.role != Witch
                        }
                        assertFalse(controller.submitAction(witchId, WitchAction(healTargetId = witchId)))
                        assertTrue(controller.submitAction(witchId, WitchAction(killTargetId = poisonTarget.id)))
                        selfHealWasRejected = true
                    }

                    else -> Unit
                }
            }
        )

        assertTrue(selfHealWasRejected)
        assertTrue(events.any { it is GameWonEvent && it.winningTeam == Team.WEREWOLVES })
    }

    @Test
    fun witchesCannotPoisonOtherWitchesWhenTheSettingForbidsIt() = runBlocking {
        val state = GameState()
        val controller = GameController(state)
        var attackedWitchId: Int? = null
        var poisonUsed = false
        var rejectedWitchTargets = 0

        playGameUntilEnd(
            controller = controller,
            state = state,
            playerCount = 4,
            settings = fastSettings(
                canWitchKillWitch = false,
                optionalRoles = mapOf(OptionalRole.WITCH to 2)
            ),
            onEvent = { gameEvent ->
                when (gameEvent.event) {
                    WerewolfTurnEvent -> {
                        val wolfId = gameEvent.recipients.single()
                        attackedWitchId = state.players.first { it.role == Witch }.id
                        assertTrue(controller.submitAction(wolfId, WerewolfAction(attackedWitchId)))
                    }

                    is WitchTurnEvent -> {
                        val actingWitchId = gameEvent.recipients.single()
                        val otherWitchId = state.players.first {
                            it.role == Witch && it.id != actingWitchId
                        }.id
                        assertFalse(
                            controller.submitAction(
                                actingWitchId,
                                WitchAction(killTargetId = otherWitchId)
                            )
                        )
                        rejectedWitchTargets++

                        if (!poisonUsed) {
                            val villagerId = state.players.single {
                                it.role?.team == Team.VILLAGE && it.role != Witch
                            }.id
                            assertTrue(
                                controller.submitAction(
                                    actingWitchId,
                                    WitchAction(killTargetId = villagerId)
                                )
                            )
                            poisonUsed = true
                        } else {
                            assertTrue(controller.submitAction(actingWitchId, WitchAction()))
                        }
                    }

                    else -> Unit
                }
            }
        )

        assertEquals(2, rejectedWitchTargets)
        assertTrue(poisonUsed)
        assertNotNull(attackedWitchId)
        Unit
    }

    private suspend fun playGameUntilEnd(
        controller: GameController,
        state: GameState,
        playerCount: Int,
        settings: GameSettings,
        onEvent: suspend (GameEvent) -> Unit
    ): List<Event> =
        playGameEventsUntilEnd(controller, state, playerCount, settings, onEvent)
            .map { it.event }

    private suspend fun playGameEventsUntilEnd(
        controller: GameController,
        state: GameState,
        playerCount: Int,
        settings: GameSettings,
        onEvent: suspend (GameEvent) -> Unit
    ): List<GameEvent> = coroutineScope {
        val eventChannel = Channel<GameEvent>(Channel.UNLIMITED)
        val collector = launch(start = CoroutineStart.UNDISPATCHED) {
            controller.events.collect(eventChannel::send)
        }
        val players = (1..playerCount).map { Player(it, "Player $it") }
        val receivedEvents = mutableListOf<GameEvent>()

        try {
            controller.start(players, settings)

            withTimeout(5.seconds) {
                do {
                    val gameEvent = eventChannel.receive()
                    receivedEvents += gameEvent
                    onEvent(gameEvent)
                } while (gameEvent.event != GameEndedEvent)
            }
        } finally {
            collector.cancelAndJoin()
            eventChannel.close()
        }

        receivedEvents
    }

    private fun fastSettings(
        werewolfAmount: Int = 1,
        canWitchUseBothPotions: Boolean = true,
        canWitchHealSelf: Boolean = true,
        canWitchKillWitch: Boolean = true,
        optionalRoles: Map<OptionalRole, Int> = emptyMap()
    ): GameSettings = GameSettings(
        werewolfAmount = werewolfAmount,
        canWitchUseBothPotions = canWitchUseBothPotions,
        canWitchHealSelf = canWitchHealSelf,
        canWitchKillWitch = canWitchKillWitch,
        voteTimeSeconds = 0,
        discussionTimeSeconds = 0,
        nightRoleActingTimeSeconds = 2,
        optionalRoles = optionalRoles
    )
}
