package com.mefabc24.werewolf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LobbyChatContent() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ChatMessage("Fabi", "Lorem ipsum", "10:16")
        ChatMessage("Alex", "dolor sit amet", "10:16")
        ChatMessage("Melly", "consetetur sadipscing elitr", "10:17")
    }
}

@Composable
fun LobbyEventContent() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        EventRow("Lobby wurde erstellt", "10:12")
        EventRow("Melly ist beigetreten", "10:13")
        EventRow("Sven ist bereit", "10:14")
        EventRow("Einstellungen wurden geändert", "10:15")
    }
}

@Composable
fun LobbySettingsContent() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SettingRow("Werwölfe", "2")
        SettingRow("Hexen", "1")
        SettingRow("Seher", "1")
        SettingRow("Diskussionszeit", "60s")
        SettingRow("Abstimmungszeit", "30s")
        SettingRow("Selbstheilende Hexen", "An")
    }
}

@Composable
private fun ChatMessage(
    name: String,
    message: String,
    time: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFF141D28),
                RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        Text(
            text = "$name: ",
            color = Color.White,
            fontSize = 14.sp
        )

        Text(
            text = message,
            color = Color.White.copy(alpha = 0.65f),
            fontSize = 14.sp
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = time,
            color = Color.White.copy(alpha = 0.4f),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun EventRow(
    text: String,
    time: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFF141D28),
                RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        Text(
            text = text,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = time,
            color = Color.White.copy(alpha = 0.4f),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun SettingRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFF141D28),
                RoundedCornerShape(8.dp)
            )
            .padding(14.dp)
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = value,
            color = Color.White.copy(alpha = 0.55f),
            fontSize = 14.sp
        )
    }
}