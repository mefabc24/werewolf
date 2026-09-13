package com.mefabc24.werewolf

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.mefabc24.werewolf.ui.screens.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Werewolf",
        state = rememberWindowState(
            width = 1920.dp,
            height = 1080.dp
        )
    ) {
        var currentScreen by remember {
            mutableStateOf(Screen.TITLE)
        }

        when (currentScreen) {
            Screen.TITLE -> {
                TitleScreen(
                    onPlayClick = {
                        currentScreen = Screen.LOBBY
                    }
                )
            }

            Screen.LOBBY -> {
                LobbyScreen()
            }
        }
    }
}