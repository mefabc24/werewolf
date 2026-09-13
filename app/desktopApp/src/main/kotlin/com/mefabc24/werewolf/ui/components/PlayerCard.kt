package com.mefabc24.werewolf.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PlayerCard(
    name: String,
    icon: Painter,
    isReady: Boolean = false,
    isHost: Boolean = false,
    isOpenSlot: Boolean = false,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(Color(0xFF141D28), shape)
            .border(
                width = 1.dp,
                color = Color(0xFF273240),
                shape = shape
            )
            .padding(horizontal = 20.dp)
            .then(
                if (isOpenSlot) Modifier.alpha(0.4f)
                else Modifier
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(28.dp)
        )

        Spacer(Modifier.width(16.dp))

        Text(
            text = name,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )

        if (isHost) {
            Spacer(Modifier.width(12.dp))

            Text(
                text = "Host",
                color = Color(0xFF08111E),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(
                        color = Color(0xFFD3263D),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(
                        horizontal = 8.dp,
                        vertical = 3.dp
                    )
            )
        }

        Spacer(Modifier.weight(1f))

        StatusIndicator(
            isReady = isReady,
            isOpenSlot = isOpenSlot
        )
    }
}

@Composable
private fun StatusIndicator(
    isReady: Boolean,
    isOpenSlot: Boolean
) {
    val color = when {
        isOpenSlot -> Color(0xFF9A9A9A)
        isReady -> Color(0xFF5BAA45)
        else -> Color(0xFFD3263D)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(
                    color = color,
                    shape = RoundedCornerShape(50)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isReady && !isOpenSlot) {
                Text(
                    text = "✓",
                    color = Color(0xFF141D28),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Text(
            text = when {
                isOpenSlot -> "Warten"
                isReady -> "Bereit"
                else -> "Nicht bereit"
            },
            color = color,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}