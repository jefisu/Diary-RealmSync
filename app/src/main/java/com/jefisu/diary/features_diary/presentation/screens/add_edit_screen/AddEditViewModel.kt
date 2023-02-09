package com.jefisu.diary.features_diary.presentation.screens.add_edit_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.jefisu.diary.core.util.Resource
import com.jefisu.diary.features_diary.domain.Diary
import com.jefisu.diary.features_diary.domain.DiaryRepository
import com.jefisu.diary.features_diary.domain.Mood
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: DiaryRepository
) : ViewModel() {

    val title = savedStateHandle.getStateFlow("title", "")
    val description = savedStateHandle.getStateFlow("description", "")
    val mood = savedStateHandle.getStateFlow("mood", Mood.Neutral)
    val images = savedStateHandle.getStateFlow("images", emptyList<String>())

    var _diary: Diary? = null
        private set

    init {
        savedStateHandle.get<String>("id")?.let {
            when (val result = repository.getDiaryById(it)) {
                is Resource.Success -> {
                    _diary = result.data
                    savedStateHandle["title"] = _diary?.title
                    savedStateHandle["description"] = _diary?.description
                    savedStateHandle["images"] = _diary?.images?.toList()
                    savedStateHandle["mood"] = Mood.valueOf(_diary!!.mood)
                }
                is Resource.Error -> Unit
            }
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