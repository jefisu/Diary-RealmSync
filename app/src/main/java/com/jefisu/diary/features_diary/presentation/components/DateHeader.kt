package com.jefisu.diary.features_diary.presentation.components

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
import java.sql.Timestamp
import java.time.ZoneId

@Composable
fun DateHeader(timestamp: Long) {

    val localDate = Timestamp(timestamp).toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = String.format("%02d", localDate.dayOfMonth),
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.Light
            )
            Text(
                text = localDate.dayOfWeek.toString().take(3),
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                fontWeight = FontWeight.Light
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text = localDate.month.toString()
                    .lowercase()
                    .replaceFirstChar { it.titlecase() },
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.Light
            )
            Text(
                text = "${localDate.year}",
                color = MaterialTheme.colorScheme.onSurface.copy(0.38f),
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                fontWeight = FontWeight.Light
            )
        }
    }
}