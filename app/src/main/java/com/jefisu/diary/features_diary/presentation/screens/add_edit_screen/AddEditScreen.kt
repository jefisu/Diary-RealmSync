package com.jefisu.diary.features_diary.presentation.screens.add_edit_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.jefisu.diary.R
import com.jefisu.diary.core.util.UiEvent
import com.jefisu.diary.core.util.UiText
import com.jefisu.diary.features_diary.domain.GalleryImage
import com.jefisu.diary.features_diary.domain.Mood
import com.jefisu.diary.features_diary.presentation.screens.add_edit_screen.components.AddEditTopBar
import com.jefisu.diary.features_diary.presentation.screens.add_edit_screen.components.GalleryUploader
import com.jefisu.diary.features_diary.presentation.screens.add_edit_screen.components.TransparentTextField
import com.jefisu.diary.features_diary.presentation.screens.add_edit_screen.components.ZoomableImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

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
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val state by viewModel.state.collectAsState()
    val pageNumber by remember {
        derivedStateOf {
            pagerState.currentPage
        }
    }
    var selectedGalleryImage by remember { mutableStateOf<GalleryImage?>(null) }

    // Update the Mood when selecting an existing Diary
    LaunchedEffect(key1 = state.mood) {
        val moodIndex = Mood.valueOf(state.mood.name).ordinal
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

    LaunchedEffect(key1 = scrollState.maxValue) {
        scrollState.scrollTo(scrollState.maxValue)
    }

    AnimatedVisibility(visible = selectedGalleryImage != null) {
        Dialog(
            onDismissRequest = { selectedGalleryImage = null }
        ) {
            if (selectedGalleryImage != null) {
                ZoomableImage(
                    selectedGalleryImage = selectedGalleryImage!!,
                    onCloseClicked = { selectedGalleryImage = null },
                    onDeleteClicked = {
                        viewModel.galleryState.removeImage(selectedGalleryImage!!)
                        selectedGalleryImage = null
                    }
                )
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            AddEditTopBar(
                state = state,
                isNewDiary = id == null,
                onBackClick = navigator::navigateUp,
                onDeleteConfirmedClick = {
                    viewModel.deleteDiary()
                    navigator.navigateUp()
                },
                onUpdateDateTime = { localDate, localTime ->
                    viewModel.onEvent(AddEditEvent.SelectDateTime(localDate, localTime))
                },
                onClickLoseChangesDate = {
                    viewModel.onEvent(AddEditEvent.RestoreDateTimeInitial)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
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
                    text = state.title,
                    onTextChange = { viewModel.onEvent(AddEditEvent.EnteredTitle(it)) },
                    hint = stringResource(R.string.title),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    )
                )
                TransparentTextField(
                    text = state.description,
                    onTextChange = { viewModel.onEvent(AddEditEvent.EnteredDescription(it)) },
                    hint = stringResource(R.string.tell_me_about_it),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            scope.launch {
                                scrollState.animateScrollTo(Int.MAX_VALUE)
                                focusManager.clearFocus()
                            }
                        }
                    )
                )
            }
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                GalleryUploader(
                    galleryState = viewModel.galleryState,
                    onClickToClearFocus = focusManager::clearFocus,
                    onImageSelect = {
                        val type = context.contentResolver.getType(it)?.split("/")?.last()
                        viewModel.addImage(
                            image = it,
                            imageType = type ?: "jpg"
                        )
                    },
                    onImageClicked = {
                        selectedGalleryImage = it
                    }
                )
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