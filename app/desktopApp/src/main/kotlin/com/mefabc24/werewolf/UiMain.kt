package com.mefabc24.werewolf

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.mefabc24.werewolf.ui.screens.TitleScreen

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Werewolf",
        state = rememberWindowState(
            width = 1920.dp,
            height = 1080.dp
        )
    ) {
        TitleScreen()
    }
}