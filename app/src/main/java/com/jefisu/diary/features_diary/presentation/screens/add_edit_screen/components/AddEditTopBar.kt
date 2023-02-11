package com.jefisu.diary.features_diary.presentation.screens.add_edit_screen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jefisu.diary.R
import com.jefisu.diary.core.util.toLocalDateTime
import com.jefisu.diary.core.util.toLocalTime
import com.jefisu.diary.features_diary.presentation.components.DisplayAlertDialog
import com.jefisu.diary.features_diary.presentation.screens.add_edit_screen.AddEditState
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockSelection
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTopBar(
    state: AddEditState,
    isNewDiary: Boolean,
    onUpdateDateTime: (LocalDate, LocalTime) -> Unit,
    onClickLoseChangesDate: () -> Unit,
    onBackClick: () -> Unit,
    onDeleteConfirmedClick: () -> Unit
) {
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    var currentTime by remember { mutableStateOf(LocalTime.now()) }
    val dateDialog = rememberSheetState()
    val timeDialog = rememberSheetState()
    var updatedDateTime by remember { mutableStateOf(false) }

    CalendarDialog(
        state = dateDialog,
        selection = CalendarSelection.Date { localDate ->
            updatedDateTime = true
            currentDate = localDate
            onUpdateDateTime(currentDate, state.timestamp.toLocalTime())
            timeDialog.show()
        },
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true
        )
    )

    ClockDialog(
        state = timeDialog,
        selection = ClockSelection.HoursMinutes { hours, minutes ->
            currentTime = LocalTime.of(hours, minutes)
            onUpdateDateTime(currentDate, currentTime)
        }
    )

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
                    text = state.mood.name,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = DateTimeFormatter
                        .ofPattern("dd MMM yyyy, hh:mm a")
                        .format(state.timestamp.toLocalDateTime())
                        .uppercase(),
                    fontSize = MaterialTheme.typography.bodySmall.fontSize
                )
            }
        },
        actions = {
            IconButton(onClick = {
                if (updatedDateTime) onClickLoseChangesDate() else dateDialog.show()
            }) {
                Icon(
                    imageVector = if (updatedDateTime) Icons.Default.Close else Icons.Default.DateRange,
                    contentDescription = "Select or Clear date",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            if (!isNewDiary) {
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