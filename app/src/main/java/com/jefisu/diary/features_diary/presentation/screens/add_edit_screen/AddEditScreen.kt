package com.jefisu.diary.features_diary.presentation.screens.add_edit_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.jefisu.diary.R
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
    val scrollState = rememberScrollState()
    val pagerState = rememberPagerState()

    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()
    val mood by viewModel.mood.collectAsState()
    val images by viewModel.images.collectAsState()

    // Update the Mood when selecting an existing Diary
    LaunchedEffect(key1 = mood) {
        val moodIndex = Mood.valueOf(mood.name).ordinal
        pagerState.scrollToPage(moodIndex)
    }

    // Update the Mood text when change current Mood icon
    LaunchedEffect(key1 = pagerState.currentPage) {
        val moodSelected = Mood.values()[pagerState.currentPage]
        viewModel.onEvent(AddEditEvent.SelectMood(moodSelected))
    }

    Scaffold(
        topBar = {
            AddEditTopBar(
                mood = mood,
                timestamp = System.currentTimeMillis(),
                diaryExist = id != null,
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
                ) { page ->
                    val moodSelected = Mood.values()[page]
                    Image(
                        painter = painterResource(id = moodSelected.icon),
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
                    onClick = { /*TODO*/ },
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