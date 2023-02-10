package com.jefisu.diary.features_diary.presentation.screens.add_edit_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.jefisu.diary.R
import com.jefisu.diary.core.util.UiEvent
import com.jefisu.diary.core.util.UiText
import com.jefisu.diary.features_diary.domain.Mood
import com.jefisu.diary.features_diary.presentation.screens.add_edit_screen.components.AddEditTopBar
import com.jefisu.diary.features_diary.presentation.screens.add_edit_screen.components.TransparentTextField
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Destination
@Composable
fun AddEditScreen(
    id: String?,
    navigator: DestinationsNavigator,
    viewModel: AddEditViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val pagerState = rememberPagerState()
    val snackBarHostState = remember { SnackbarHostState() }

    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()
    val mood by viewModel.mood.collectAsState()
    val images by viewModel.images.collectAsState()
    val diary by viewModel.diary.collectAsState()

    val pageNumber by remember {
        derivedStateOf {
            pagerState.currentPage
        }
    }

    // Update the Mood when selecting an existing Diary
    LaunchedEffect(key1 = Unit) {
        val moodIndex = Mood.valueOf(mood).ordinal
        pagerState.scrollToPage(moodIndex)
    }
    LaunchedEffect(key1 = pageNumber) {
        viewModel.onEvent(AddEditEvent.SelectMood(Mood.values()[pageNumber]))
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is UiEvent.Navigate -> navigator.navigateUp()
                is UiEvent.ShowError -> {
                    snackBarHostState.showSnackbar(
                        (event.uiText ?: UiText.unknownError()).asString(context)
                    )
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            AddEditTopBar(
                diary = diary,
                moodName = { Mood.values()[pageNumber].name },
                onBackClick = navigator::navigateUp,
                onDeleteConfirmedClick = { }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = 16.dp
                )
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                HorizontalPager(
                    count = Mood.values().size,
                    state = pagerState
                ) {
                    Image(
                        painter = painterResource(id = Mood.values()[pageNumber].icon),
                        contentDescription = "Mood image",
                        modifier = Modifier.size(120.dp)
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))
                TransparentTextField(
                    text = title,
                    onTextChange = { viewModel.onEvent(AddEditEvent.EnteredTitle(it)) },
                    hint = stringResource(R.string.title),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = {}
                    )
                )
                TransparentTextField(
                    text = description,
                    onTextChange = { viewModel.onEvent(AddEditEvent.EnteredDescription(it)) },
                    hint = stringResource(R.string.tell_me_about_it),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = {}
                    )
                )
            }
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = viewModel::insertDiary,
                    shape = Shapes().small,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                ) {
                    Text(text = stringResource(R.string.save))
                }
            }
        }
    }
}