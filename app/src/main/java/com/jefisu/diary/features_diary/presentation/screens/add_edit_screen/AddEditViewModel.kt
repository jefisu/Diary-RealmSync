package com.jefisu.diary.features_diary.presentation.screens.add_edit_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jefisu.diary.R
import com.jefisu.diary.core.util.Resource
import com.jefisu.diary.core.util.UiEvent
import com.jefisu.diary.core.util.UiText
import com.jefisu.diary.features_diary.domain.Diary
import com.jefisu.diary.features_diary.domain.DiaryRepository
import com.jefisu.diary.features_diary.domain.Mood
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: DiaryRepository
) : ViewModel() {

    val title = savedStateHandle.getStateFlow("title", "")
    val description = savedStateHandle.getStateFlow("description", "")
    val images = savedStateHandle.getStateFlow("images", emptyList<String>())
    val mood = savedStateHandle.getStateFlow("mood", Mood.Neutral.name)

    private val _event = Channel<UiEvent>()
    val event = _event.receiveAsFlow()

    private val _diary = MutableStateFlow<Diary?>(null)
    val diary = _diary.asStateFlow()

    init {
        savedStateHandle.get<String>("id")?.let {
            repository.getDiaryById(it)
                .onEach { result ->
                    if (result is Resource.Success) {
                        _diary.update { result.data }
                        savedStateHandle["title"] = _diary.value?.title
                        savedStateHandle["description"] = _diary.value?.description
                        savedStateHandle["images"] = _diary.value?.images?.toList()
                        savedStateHandle["mood"] = _diary.value?.mood
                    }
                }.launchIn(viewModelScope)
        }
    }

    fun onEvent(event: AddEditEvent) {
        when (event) {
            is AddEditEvent.EnteredTitle -> {
                savedStateHandle["title"] = event.value
            }

            is AddEditEvent.EnteredDescription -> {
                savedStateHandle["description"] = event.value
            }

            is AddEditEvent.SelectMood -> {
                savedStateHandle["mood"] = event.mood.name
            }
        }
    }

    fun insertDiary() {
        viewModelScope.launch {
            if (title.value.isEmpty() || description.value.isEmpty()) {
                _event.send(
                    UiEvent.ShowError(
                        UiText.StringResource(R.string.fields_can_t_be_empty)
                    )
                )
                return@launch
            }
            val newDiary = Diary().apply {
                title = this@AddEditViewModel.title.value
                description = this@AddEditViewModel.description.value
                mood = this@AddEditViewModel.mood.value
                images = this@AddEditViewModel.images.value.toRealmList()
            }
            if (_diary.value == null) {
                repository.insertDiary(newDiary).also { result ->
                    if (result is Resource.Success) {
                        _diary.update { result.data }
                        _event.send(UiEvent.Navigate())
                    } else {
                        _event.send(
                            UiEvent.ShowError(
                                (result as Resource.Error).uiText
                            )
                        )
                    }
                }
            } else {
                repository.updateDiary(newDiary.apply { _id = _diary.value!!._id }).also { result ->
                    if (result is Resource.Success) {
                        _diary.update { result.data }
                        _event.send(UiEvent.Navigate())
                    } else {
                        _event.send(
                            UiEvent.ShowError(
                                (result as Resource.Error).uiText
                            )
                        )
                    }
                }
            }
        }
    }
}