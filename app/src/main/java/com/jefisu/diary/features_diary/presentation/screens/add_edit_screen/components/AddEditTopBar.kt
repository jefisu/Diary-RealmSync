package com.jefisu.diary.features_diary.presentation.screens.add_edit_screen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jefisu.diary.R
import com.jefisu.diary.features_diary.presentation.components.DisplayAlertDialog
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTopBar(
    diaryExist: Boolean,
    moodName: () -> String,
    timestamp: Long,
    onBackClick: () -> Unit,
    onDeleteConfirmedClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Go back screen"
                )
            }
        },
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = moodName(),
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                        .format(timestamp),
                    fontSize = MaterialTheme.typography.bodySmall.fontSize
                )
            }
        },
        actions = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Date icon",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            if (diaryExist) {
                DeleteDiaryAction(
                    onDeleteConfirmedClick = onDeleteConfirmedClick
                )
            }
        }
    )
}

@Composable
fun DeleteDiaryAction(
    onDeleteConfirmedClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var openDialog by remember { mutableStateOf(false) }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(
            text = {
                Text(text = stringResource(R.string.delete))
            },
            onClick = {
                openDialog = true
                expanded = false
            }
        )
    }
    DisplayAlertDialog(
        title = stringResource(R.string.delete),
        message = stringResource(
            R.string.are_you_sure_you_want_to_permanently_delete_this_note_s,
        ),
        isOpened = openDialog,
        onCloseDialog = { openDialog = false },
        onConfirmClick = onDeleteConfirmedClick
    )
}