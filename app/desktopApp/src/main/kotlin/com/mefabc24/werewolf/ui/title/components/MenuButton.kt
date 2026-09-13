package com.mefabc24.werewolf.ui.title.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MenuButton(
    text: String,
    icon: Painter,
    onClick: () -> Unit,
    containerColor: Color = Color(0xFF141D28),
    borderColor: Color = Color(0xFF272F39),
) {
    val shape = RoundedCornerShape(16.dp)

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = shape
            ),
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor
        ),
        contentPadding = PaddingValues(
            horizontal = 24.dp,
            vertical = 0.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .align(Alignment.CenterStart),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp)
                )
            }

            Text(
                text = text,
                fontSize = 34.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}