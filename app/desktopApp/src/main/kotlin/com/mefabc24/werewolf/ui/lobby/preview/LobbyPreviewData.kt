package com.mefabc24.werewolf.ui.lobby.preview

import com.mefabc24.werewolf.resources.*
import com.mefabc24.werewolf.ui.lobby.model.ChatMessageUi
import com.mefabc24.werewolf.ui.lobby.model.LobbyEventUi
import com.mefabc24.werewolf.ui.lobby.model.LobbyInfoUi
import com.mefabc24.werewolf.ui.lobby.model.LobbyPlayerUi
import com.mefabc24.werewolf.ui.lobby.model.LobbyUiData
import com.mefabc24.werewolf.ui.lobby.model.SettingTone
import com.mefabc24.werewolf.ui.lobby.model.SettingUi
import com.mefabc24.werewolf.ui.lobby.model.SettingsSectionUi

/** The only source of sample lobby data; replace at the screen boundary later. */
object LobbyPreviewData {
    val lobby = LobbyUiData(
        info = LobbyInfoUi(
            lobbyCode = "C8F9ACB5",
            hostName = "Fabi",
            playerCount = 6,
            maxPlayers = 8,
            readyCount = 3,
        ),
        players = listOf(
            LobbyPlayerUi("fabi", "Fabi", isHost = true, isReady = true),
            LobbyPlayerUi("melly", "Melly"),
            LobbyPlayerUi("sven", "Sven", isReady = true),
            LobbyPlayerUi("lenny", "Lenny", isReady = true),
            LobbyPlayerUi("alex", "Alex"),
            LobbyPlayerUi("fjolla", "Fjolla"),
            LobbyPlayerUi("open-1", "Offener Platz", isOpenSlot = true),
            LobbyPlayerUi("open-2", "Offener Platz", isOpenSlot = true),
        ),
        messages = listOf(
            ChatMessageUi("chat-1", "Fabi", "Lorem ipsum", "10:16"),
            ChatMessageUi("chat-2", "Melly", "dolor sit amet", "10:16"),
            ChatMessageUi("chat-3", "Sven", "consetetur sadipscing elitr, sed diam nonumy", "10:17"),
            ChatMessageUi("chat-4", "Alex", "eirmod tempor invidunt ut labore", "10:18"),
            ChatMessageUi("chat-5", "Fabi", "et dolore magna", "10:18"),
            ChatMessageUi("chat-6", "Fjolla", "aliquyam erat, sed diam voluptua.", "10:18"),
            ChatMessageUi("chat-7", "Lenny", "At vero eos et accusam et justo duo dolores", "10:18"),
        ),
        events = listOf(
            LobbyEventUi("event-1", Res.drawable.crown, "Fabi hat die Lobby erstellt.", "10:16"),
            LobbyEventUi("event-2", Res.drawable.add_user, "Melly ist der Lobby beigetreten.", "10:16"),
            LobbyEventUi("event-3", Res.drawable.circle_check, "Sven ist bereit.", "10:17"),
            LobbyEventUi("event-4", Res.drawable.add_user, "Alex ist der Lobby beigetreten.", "10:18"),
            LobbyEventUi("event-5", Res.drawable.settings, "Fabi hat die Einstellungen bearbeitet.", "10:18"),
            LobbyEventUi("event-6", Res.drawable.minus, "Lenny ist nicht mehr bereit.", "10:18"),
            LobbyEventUi("event-7", Res.drawable.remove_user, "Fjolla hat die Lobby verlassen.", "10:20"),
            // TODO(icon): Replace settings with the refresh/game-mode resource when available.
            LobbyEventUi("event-8", Res.drawable.settings, "Der Spielmodus wurde aktualisiert.", "10:21"),
        ),
        settings = listOf(
            SettingsSectionUi(
                id = "roles",
                title = "Spielerrollen",
                // TODO(icons): Replace the requested settings placeholders with role resources.
                settings = listOf(
                    SettingUi("werewolves", Res.drawable.settings, "Werwölfe", "2"),
                    SettingUi("witches", Res.drawable.settings, "Hexen", "1"),
                    SettingUi("seers", Res.drawable.settings, "Seher", "1"),
                ),
            ),
            SettingsSectionUi(
                id = "flow",
                title = "Spielablauf",
                settings = listOf(
                    SettingUi("discussion", Res.drawable.settings, "Diskussionszeit", "60s"),
                    SettingUi("voting", Res.drawable.settings, "Abstimmungszeit", "30s"),
                    SettingUi("self-heal", Res.drawable.settings, "Selbstheilende Hexen", "An", SettingTone.POSITIVE),
                    SettingUi("win-condition", Res.drawable.settings, "Gewinnkonditionen", "Klassisch"),
                ),
            ),
        ),
    )
}
