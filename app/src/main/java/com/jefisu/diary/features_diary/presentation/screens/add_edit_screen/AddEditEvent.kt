package com.jefisu.diary.features_diary.presentation.screens.add_edit_screen

import com.jefisu.diary.features_diary.domain.Mood
import java.time.LocalDate
import java.time.LocalTime

sealed class AddEditEvent {
    data class EnteredTitle(val value: String) : AddEditEvent()
    data class EnteredDescription(val value: String) : AddEditEvent()
    data class SelectMood(val mood: Mood) : AddEditEvent()
    data class SelectDateTime(val localDate: LocalDate, val localTime: LocalTime) : AddEditEvent()
    object RestoreDateTimeInitial : AddEditEvent()
}
