package com.jefisu.diary.features_diary.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jefisu.diary.R
import com.jefisu.diary.features_diary.presentation.components.DateHeader
import com.jefisu.diary.features_diary.presentation.components.DiaryHolder
import com.jefisu.diary.features_diary.presentation.components.DiaryTopBar
import com.jefisu.diary.features_diary.presentation.components.DisplayAlertDialog
import com.jefisu.diary.features_diary.presentation.components.NavigationDrawer
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Destination
@Composable
fun DiaryScreen(
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var signOutDialogOpened by remember { mutableStateOf(false) }
    val state = DiaryState()

    DisplayAlertDialog(
        title = stringResource(R.string.sign_out),
        message = stringResource(R.string.alert_sign_out_message),
        isOpened = signOutDialogOpened,
        onCloseDialog = { signOutDialogOpened = false },
        onConfirmClick = {
        }
    )

    NavigationDrawer(
        drawerState = drawerState,
        onSignOutClick = { signOutDialogOpened = true }
    ) {
        Scaffold(
            topBar = {
                DiaryTopBar(onMenuClick = {
                    scope.launch { drawerState.open() }
                })
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "New diary icon"
                    )
                }
            }
        ) {
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                    return@Scaffold
                }

                state.error != null -> {
                    EmptyContent(
                        title = "Error",
                        subtitle = state.error.asString()
                    )
                    return@Scaffold
                }

                state.diaries.isEmpty() -> {
                    EmptyContent(
                        title = stringResource(R.string.empty_diary),
                        subtitle = stringResource(R.string.write_something),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    )
                    return@Scaffold
                }
            }
            LazyColumn(
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                state.diaries.forEach { (timestamp, diaries) ->
                    stickyHeader {
                        DateHeader(timestamp = timestamp)
                    }
                    items(
                        items = diaries,
                        key = { it._id.toString() }
                    ) { diary ->
                        DiaryHolder(
                            diary = diary,
                            onClick = { }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyContent(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = title,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = subtitle,
            fontSize = MaterialTheme.typography.bodyLarge.fontSize,
            fontWeight = FontWeight.Normal
        )
    }
}