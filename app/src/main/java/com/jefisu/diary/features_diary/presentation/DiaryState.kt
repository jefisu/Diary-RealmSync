package com.jefisu.diary.features_diary.presentation

import com.jefisu.diary.core.util.UiText
import com.jefisu.diary.features_diary.domain.Diary
import java.time.LocalDate

data class DiaryState(
    val isLoading: Boolean = false,
    val diaries: Map<LocalDate, List<Diary>> = emptyMap(),
    val error: UiText? = null
)