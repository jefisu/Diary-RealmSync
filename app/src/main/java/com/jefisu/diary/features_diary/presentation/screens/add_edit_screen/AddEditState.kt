package com.jefisu.diary.features_diary.presentation.screens.add_edit_screen

import com.jefisu.diary.features_diary.domain.Mood

data class AddEditState(
    val title: String = "",
    val description: String = "",
    val images: List<String> = emptyList(),
    val mood: Mood = Mood.Neutral,
    val timestamp: Long = System.currentTimeMillis()
)