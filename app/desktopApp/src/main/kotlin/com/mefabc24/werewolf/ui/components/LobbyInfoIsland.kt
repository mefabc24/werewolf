package com.mefabc24.werewolf.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LobbyInfoIsland(
    lobbyCode: String,
    hostName: String,
    playerCount: Int,
    maxPlayers: Int,
    readyCount: Int,
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(16.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .background(Color(0xFF07101D), shape)
            .border(1.dp, Color(0xFF272F3A), shape)
            .padding(horizontal = 28.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LobbyInfoItem(
            value = lobbyCode,
            label = "Lobby-Code",
            modifier = Modifier.weight(1.5f)
        )

        LobbyDivider()

        LobbyInfoItem(
            value = hostName,
            label = "Host",
            modifier = Modifier.weight(1f)
        )

        LobbyDivider()

        LobbyInfoItem(
            value = "$playerCount / $maxPlayers",
            label = "Spieler",
            modifier = Modifier.weight(1f)
        )

        LobbyDivider()

        LobbyInfoItem(
            value = "$readyCount / $playerCount",
            label = "Bereitschaft",
            modifier = Modifier.weight(1f)
        )

        LobbyDivider()

        Button(
            onClick = onStartClick,
            modifier = Modifier
                .width(190.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            border = BorderStroke(
                1.dp,
                Color(0xFFB93636)
            )
        ) {
            Text(
                text = "▷  Spiel starten",
                color = Color(0xFFD62F3F)
            )
        }
    }
}

@Composable
private fun RowScope.LobbyInfoItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = value,
            color = Color.White,
            fontSize = 24.sp
        )

        Text(
            text = label,
            color = Color.White.copy(alpha = 0.45f),
            fontSize = 15.sp
        )
    }
}

@Composable
private fun LobbyDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(76.dp)
            .background(Color(0xFF272F3A))
    )
}