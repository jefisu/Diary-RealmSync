package com.jefisu.diary.features_diary.presentation.screens.add_edit_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jefisu.diary.features_diary.domain.Mood
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

//    private val _navArg = savedStateHandle.navArgs<AddEditNavArgs>()

    private val _title = savedStateHandle.getStateFlow("title", "")
    private val _description = savedStateHandle.getStateFlow("description", "")
    private val _mood = savedStateHandle.getStateFlow("mood", Mood.Neutral)
    private val _images = savedStateHandle.getStateFlow("images", emptyList<String>())

    val state = combine(_title, _description, _mood, _images) { title, description, mood, images ->
        AddEditState(
            title = title,
            description = description,
            mood = mood,
            images = images
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), AddEditState())

    init {
        savedStateHandle.get<String>("id")?.let {
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
                savedStateHandle["mood"] = event.mood
            }
        }
    }

    fun saveDiary() {
    }
}