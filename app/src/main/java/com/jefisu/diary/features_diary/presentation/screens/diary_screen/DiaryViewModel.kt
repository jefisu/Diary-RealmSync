package com.jefisu.diary.features_diary.presentation.screens.diary_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jefisu.diary.core.util.Resource
import com.jefisu.diary.core.util.UiText
import com.jefisu.diary.features_diary.domain.Diary
import com.jefisu.diary.features_diary.domain.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val repository: DiaryRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val diaries = savedStateHandle.getStateFlow("diaries", listOf<Diary>())

    private val _error = MutableStateFlow<UiText?>(null)
    val error = _error.asStateFlow()

    init {
        getAllDiaries()
    }

    private fun getAllDiaries() {
        repository.getAllDiaries()
            .onEach { result ->
                when (result) {
                    is Resource.Success -> savedStateHandle["diaries"] = result.data?.toList()
                    is Resource.Error -> _error.update { result.uiText }
                }
            }.launchIn(viewModelScope)
    }
}