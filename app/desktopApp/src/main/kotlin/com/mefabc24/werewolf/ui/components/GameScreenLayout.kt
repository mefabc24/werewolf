package com.mefabc24.werewolf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GameScreenLayout(
    title: String,
    centerHeaderContent: (@Composable () -> Unit)? = null,
    onSettingsClick: () -> Unit,
    sidebarContent: @Composable () -> Unit,
    mainContent: @Composable () -> Unit
) {
    AppShell {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AppHeader(
                title = title,
                centerContent = centerHeaderContent,
                onSettingsClick = onSettingsClick
            )

            // Line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFF272F3A))
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .weight(0.30f)
                        .fillMaxHeight()
                ) {
                    sidebarContent()
                }

                // Line
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(Color(0xFF272F3A))
                )

                Box(
                    modifier = Modifier
                        .weight(0.70f)
                        .fillMaxHeight()
                        .background(Color(0xFF0E1924))
                        .padding(16.dp)
                ) {
                    mainContent()
                }
            }
        }
    }
}