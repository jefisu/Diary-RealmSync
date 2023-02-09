package com.jefisu.diary.features_diary.presentation.screens.add_edit_screen

import com.jefisu.diary.features_diary.domain.Mood

sealed class AddEditEvent {
    data class EnteredTitle(val value: String) : AddEditEvent()
    data class EnteredDescription(val value: String) : AddEditEvent()
    data class SelectMood(val mood: Mood) : AddEditEvent()
}
