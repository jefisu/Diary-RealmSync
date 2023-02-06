package com.jefisu.diary.features_diary.presentation.screens.diary_screen.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.jefisu.diary.R
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onMenuClick: () -> Unit,
    isDateSelected: Boolean,
    onSelectDate: (LocalDate?) -> Unit
) {
    val dateDialogState = rememberSheetState()

    CalendarDialog(
        state = dateDialogState,
        selection = CalendarSelection.Date(
            onSelectDate = onSelectDate
        ),
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true
        )
    )

    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = {
            Text(text = stringResource(R.string.app_name))
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Hamburger Menu Icon",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        actions = {
            IconButton(onClick = {
                if (isDateSelected) {
                    onSelectDate(null)
                    return@IconButton
                }
                dateDialogState.show()
            }) {
                Icon(
                    imageVector = if (isDateSelected) Icons.Default.Close else Icons.Default.DateRange,
                    contentDescription = "Clear date selected/Select date icon",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}