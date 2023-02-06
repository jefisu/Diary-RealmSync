package com.jefisu.diary.core.util

import com.ramcosta.composedestinations.spec.Direction

sealed class UiEvent {
    data class Navigate(val direction: Direction? = null) : UiEvent()
    data class ShowError(val uiText: UiText? = null) : UiEvent()
}