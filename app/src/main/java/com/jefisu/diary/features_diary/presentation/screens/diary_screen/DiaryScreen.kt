package com.jefisu.diary.features_diary.presentation.screens.diary_screen

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jefisu.diary.R
import com.jefisu.diary.core.util.toLocalDate
import com.jefisu.diary.destinations.AddEditScreenDestination
import com.jefisu.diary.destinations.AuthScreenDestination
import com.jefisu.diary.features_diary.presentation.components.DisplayAlertDialog
import com.jefisu.diary.features_diary.presentation.screens.diary_screen.components.DateHeader
import com.jefisu.diary.features_diary.presentation.screens.diary_screen.components.DiaryHolder
import com.jefisu.diary.features_diary.presentation.screens.diary_screen.components.DiaryTopBar
import com.jefisu.diary.features_diary.presentation.screens.diary_screen.components.NavigationDrawer
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.navigate
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Destination
@Composable
fun DiaryScreen(
    navController: NavController,
    onDataLoaded: () -> Unit,
    viewModel: DiaryViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var signOutDialogOpened by remember { mutableStateOf(false) }
    val diaries by viewModel.diaries.collectAsState()
    val error by viewModel.error.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(key1 = diaries) {
        if (diaries.isNotEmpty()) {
            onDataLoaded()
        }
    }

    DisplayAlertDialog(
        title = stringResource(R.string.sign_out),
        message = stringResource(R.string.alert_sign_out_message),
        isOpened = signOutDialogOpened,
        onCloseDialog = { signOutDialogOpened = false },
        onConfirmClick = {
            viewModel.signOut()
            navController.navigate(AuthScreenDestination)
        }
    )

    NavigationDrawer(
        drawerState = drawerState,
        onSignOutClick = { signOutDialogOpened = true }
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                DiaryTopBar(
                    scrollBehavior = scrollBehavior,
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    navController.navigate(AddEditScreenDestination(null))
                }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "New diary icon"
                    )
                }
            }
        ) { paddingValues ->
            if (error != null) {
                EmptyContent(
                    title = "Error",
                    subtitle = error!!.asString()
                )
                return@Scaffold
            } else if (diaries.isEmpty()) {
                EmptyContent(
                    title = stringResource(R.string.empty_diary),
                    subtitle = stringResource(R.string.write_something),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                )
                return@Scaffold
            }
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .navigationBarsPadding()
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                    )
            ) {
                diaries
                    .groupBy { it.timestamp.toLocalDate() }
                    .forEach { (localDate, diaries) ->
                        stickyHeader {
                            DateHeader(
                                localDate = localDate,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                        items(
                            items = diaries,
                            key = { it._id.toString() }
                        ) { diary ->
                            DiaryHolder(
                                diary = diary,
                                onClick = {
                                    navController.navigate(
                                        AddEditScreenDestination(diary._id.toString())
                                    )
                                }
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