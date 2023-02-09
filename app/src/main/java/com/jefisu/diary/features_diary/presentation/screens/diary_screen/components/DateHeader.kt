package com.jefisu.diary.features_diary.presentation.screens.diary_screen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime

@Composable
fun DateHeader(localDateTime: LocalDateTime, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = String.format("%02d", localDateTime.dayOfMonth),
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.Light
            )
            Text(
                text = localDateTime.dayOfWeek.toString().take(3),
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                fontWeight = FontWeight.Light
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text = localDateTime.month.toString()
                    .lowercase()
                    .replaceFirstChar { it.titlecase() },
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.Light
            )
            Text(
                text = "${localDateTime.year}",
                color = MaterialTheme.colorScheme.onSurface.copy(0.38f),
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                fontWeight = FontWeight.Light
            )
        }
    }
}