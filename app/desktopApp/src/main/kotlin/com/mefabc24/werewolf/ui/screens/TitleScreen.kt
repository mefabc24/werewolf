package com.mefabc24.werewolf.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import com.mefabc24.werewolf.resources.Res
import com.mefabc24.werewolf.resources.bg
import com.mefabc24.werewolf.resources.quit
import com.mefabc24.werewolf.resources.lobby
import com.mefabc24.werewolf.resources.settings
import com.mefabc24.werewolf.resources.title
import com.mefabc24.werewolf.resources.wolf
import com.mefabc24.werewolf.ui.components.MenuButton

@Composable
fun TitleScreen() {
    AppShell {
        Image(
            painter = painterResource(Res.drawable.bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .wrapContentHeight()
                .align(Alignment.Center)
        ) {
            Image(
                painter = painterResource(Res.drawable.title),
                contentDescription = null,
                modifier = Modifier.width(780.dp)
                    .align(Alignment.CenterHorizontally)
                    .aspectRatio(3f),
                contentScale = ContentScale.Fit
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth(0.30f)
                    .wrapContentHeight()
                    .padding(0.dp, 32.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                MenuButton(
                    text = "Spielen",
                    icon = painterResource(Res.drawable.wolf),
                    containerColor = Color(0xFF8E2525),
                    borderColor = Color(0xFFB93636),
                    onClick = { }
                )

                MenuButton(
                    text = "Lobby beitreten",
                    icon = painterResource(Res.drawable.lobby),
                    onClick = { }
                )

                MenuButton(
                    text = "Einstellungen",
                    icon = painterResource(Res.drawable.settings),
                    onClick = { }
                )

                MenuButton(
                    text = "Beenden",
                    icon = painterResource(Res.drawable.quit),
                    onClick = { }
                )
            }
        }
    }
}
