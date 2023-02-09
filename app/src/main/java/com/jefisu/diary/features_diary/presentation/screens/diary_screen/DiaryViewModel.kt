package com.jefisu.diary.features_diary.presentation.screens.diary_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jefisu.diary.core.util.Resource
import com.jefisu.diary.core.util.UiText
import com.jefisu.diary.core.util.toLocalDateTime
import com.jefisu.diary.features_diary.domain.Diary
import com.jefisu.diary.features_diary.domain.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val repository: DiaryRepository
) : ViewModel() {

    private val _diaries = MutableStateFlow(emptyList<Diary>())
    private val _error = MutableStateFlow<UiText?>(null)
    private val _isLoading = MutableStateFlow(false)

    val state = combine(_diaries, _error, _isLoading) { diaries, error, isLoading ->
        DiaryState(
            error = error,
            isLoading = isLoading,
            diaries = diaries.groupBy {
                it.timestamp.toLocalDateTime()
            }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), DiaryState())

    init {
        getAllDiaries()
    }

    fun getAllDiaries() {
        repository.getAllDiaries()
            .onStart { _isLoading.update { true } }
            .onEach { result ->
                _isLoading.update { false }
                when (result) {
                    is Resource.Success -> {
                        _diaries.update { result.data ?: emptyList() }
                    }

                    is Resource.Error -> {
                        _error.update { result.uiText }
                    }
                }
            }
            .launchIn(viewModelScope)
    }
}