package com.jefisu.diary.features_auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jefisu.diary.core.util.Resource
import com.jefisu.diary.core.util.UiText
import com.jefisu.diary.features_auth.domain.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    @Named("CLIENT_ID") val clientId: String
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun signInWithMongoAtlas(
        tokenId: String,
        onResult: (UiText?) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.update { true }
            when (val result = repository.signIn(tokenId)) {
                is Resource.Success -> Unit
                is Resource.Error -> onResult(result.uiText)
            }
            _isLoading.update { false }
        }
    }
}