package com.jefisu.diary.features_auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jefisu.diary.core.util.Resource
import com.jefisu.diary.core.util.UiEvent
import com.jefisu.diary.destinations.DiaryScreenDestination
import com.jefisu.diary.features_auth.domain.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    @Named("CLIENT_ID") val clientId: String
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun signInWithMongoAtlas(tokenId: String) {
        viewModelScope.launch {
            _isLoading.update { true }
            when (val result = repository.signIn(tokenId)) {
                is Resource.Success -> {
                    _uiEvent.send(UiEvent.Navigate(DiaryScreenDestination))
                }
                is Resource.Error -> {
                    _uiEvent.send(UiEvent.ShowError(result.uiText))
                }
            }
            _isLoading.update { false }
        }
    }
}