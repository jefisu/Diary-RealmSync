package com.jefisu.diary.features_auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.jefisu.diary.core.util.Resource
import com.jefisu.diary.core.util.UiEvent
import com.jefisu.diary.core.util.UiText
import com.jefisu.diary.features_auth.domain.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    @Named("CLIENT_ID") val clientId: String
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private fun signInWithMongoAtlas(tokenId: String) {
        viewModelScope.launch {
            val result = repository.signIn(tokenId)
            _uiEvent.send(
                when (result) {
                    is Resource.Success -> UiEvent.Navigate()
                    is Resource.Error -> UiEvent.ShowError(result.uiText)
                }
            )
            _isLoading.update { false }
        }
    }

    fun signInFirebase(tokenId: String) {
        _isLoading.update { true }
        val credential = GoogleAuthProvider.getCredential(tokenId, null)
        FirebaseAuth.getInstance()
            .signInWithCredential(credential)
            .addOnCompleteListener { task ->
                viewModelScope.launch {
                    if (task.isSuccessful) {
                        signInWithMongoAtlas(tokenId)
                    } else {
                        val error = UiText.DynamicString(task.exception.toString())
                        _uiEvent.send(
                            UiEvent.ShowError(error)
                        )
                        _isLoading.update { false }
                    }
                }
            }
    }
}