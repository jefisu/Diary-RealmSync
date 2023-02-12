package com.jefisu.diary.features_diary.presentation.screens.diary_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.jefisu.diary.core.connectivity.ConnectivityObserver
import com.jefisu.diary.core.util.Resource
import com.jefisu.diary.core.util.UiText
import com.jefisu.diary.features_diary.data.database.ImageToDeleteDao
import com.jefisu.diary.features_diary.data.database.entity.ImageToDelete
import com.jefisu.diary.features_diary.domain.Diary
import com.jefisu.diary.features_diary.domain.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val repository: DiaryRepository,
    private val savedStateHandle: SavedStateHandle,
    private val app: App,
    private val connectivity: ConnectivityObserver,
    private val imageToDeleteDao: ImageToDeleteDao
) : ViewModel() {

    private var network by mutableStateOf(ConnectivityObserver.Status.Unavailable)

    val diaries = savedStateHandle.getStateFlow("diaries", listOf<Diary>())

    private val _message = Channel<UiText>()
    val message = _message.receiveAsFlow()

    init {
        getAllDiaries()
    }

    private fun getAllDiaries() {
        repository.getAllDiaries()
            .onEach { result ->
                when (result) {
                    is Resource.Success -> savedStateHandle["diaries"] = result.data?.toList()
                    is Resource.Error -> _message.send(result.uiText)
                }
            }.launchIn(viewModelScope)
    }

    fun signOut() {
        viewModelScope.launch {
            app.currentUser?.logOut()
        }
    }

    fun deleteAllDiaries() {
        if (network == ConnectivityObserver.Status.Available) {
            viewModelScope.launch {
                _message.send(UiText.DynamicString("No Internet Connection"))
            }
            return
        }

        val userIdFirebase = FirebaseAuth.getInstance().currentUser?.uid
        val imageDirectory = "images/$userIdFirebase"
        val storage = FirebaseStorage.getInstance().reference

        storage.child(imageDirectory)
            .listAll()
            .addOnSuccessListener {
                it.items.forEach { ref ->
                    val imagePath = "$imageDirectory/${ref.name}"
                    storage.child(imagePath).delete()
                        .addOnFailureListener {
                            viewModelScope.launch {
                                imageToDeleteDao.addImageToDelete(
                                    ImageToDelete(remoteImagePath = imagePath)
                                )
                            }
                        }
                }
                viewModelScope.launch {
                    val result = repository.deleteAllDiaries()
                    _message.send(
                        when (result) {
                            is Resource.Error -> result.uiText
                            is Resource.Success -> UiText.DynamicString("All diaries deleted")
                        }
                    )
                }
            }
            .addOnFailureListener { error ->
                viewModelScope.launch {
                    _message.send(UiText.DynamicString(error.message.toString()))
                }
            }
    }
}