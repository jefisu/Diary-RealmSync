package com.jefisu.diary.features_diary.presentation.screens.diary_screen

import com.jefisu.diary.core.util.UiText
import com.jefisu.diary.features_diary.domain.Diary
import java.time.LocalDateTime

data class DiaryState(
    val isLoading: Boolean = false,
    val diaries: Map<LocalDateTime, List<Diary>> = emptyMap(),
    val error: UiText? = null
)