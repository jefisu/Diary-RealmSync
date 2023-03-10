package com.jefisu.diary.features_diary.presentation.screens.add_edit_screen

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.jefisu.diary.R
import com.jefisu.diary.core.util.Resource
import com.jefisu.diary.core.util.UiEvent
import com.jefisu.diary.core.util.UiText
import com.jefisu.diary.core.util.fetchImagesFromFirebase
import com.jefisu.diary.features_diary.data.database.ImageToDeleteDao
import com.jefisu.diary.features_diary.data.database.ImageToUploadDao
import com.jefisu.diary.features_diary.data.database.entity.ImageToDelete
import com.jefisu.diary.features_diary.data.database.entity.ImageToUploadEntity
import com.jefisu.diary.features_diary.domain.Diary
import com.jefisu.diary.features_diary.domain.DiaryRepository
import com.jefisu.diary.features_diary.domain.GalleryImage
import com.jefisu.diary.features_diary.domain.GalleryState
import com.jefisu.diary.features_diary.domain.Mood
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: DiaryRepository,
    private val imageToUploadDao: ImageToUploadDao,
    private val imageToDeleteDao: ImageToDeleteDao
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditState())
    val state = _state.asStateFlow()

    private val _event = Channel<UiEvent>()
    val event = _event.receiveAsFlow()

    val galleryState = GalleryState()

    private var _diary: Diary? = null

    init {
        savedStateHandle.get<String>("id")?.let {
            repository.getDiaryById(it)
                .onEach { result ->
                    if (result is Resource.Success && result.data != null) {
                        _diary = result.data
                        _state.update { state ->
                            state.copy(
                                title = result.data.title,
                                description = result.data.description,
                                mood = Mood.valueOf(result.data.mood),
                                timestamp = result.data.timestamp
                            )
                        }
                        fetchImagesFromFirebase(
                            remoteImagePaths = result.data.images,
                            onResultDownload = { downloadedImageResult ->
                                if (downloadedImageResult is Resource.Success) {
                                    galleryState.addImage(
                                        GalleryImage(
                                            image = downloadedImageResult.data!!,
                                            remoteImagePath = extractImagePath(downloadedImageResult.toString())
                                        )
                                    )
                                }
                            }
                        )
                    }
                }.launchIn(viewModelScope)
        }
    }

    fun onEvent(event: AddEditEvent) {
        when (event) {
            is AddEditEvent.EnteredTitle -> {
                _state.update { it.copy(title = event.value) }
            }

            is AddEditEvent.EnteredDescription -> {
                _state.update { it.copy(description = event.value) }
            }

            is AddEditEvent.SelectMood -> {
                _state.update { it.copy(mood = event.mood) }
            }

            is AddEditEvent.SelectDateTime -> {
                val timestamp = ZonedDateTime
                    .of(event.localDate, event.localTime, ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()
                _state.update {
                    it.copy(timestamp = timestamp)
                }
            }

            AddEditEvent.RestoreDateTimeInitial -> {
                _state.update {
                    it.copy(timestamp = _diary?.timestamp ?: System.currentTimeMillis())
                }
            }
        }
    }

    fun insertDiary() {
        viewModelScope.launch {
            if (state.value.title.isEmpty() || state.value.description.isEmpty()) {
                _event.send(
                    UiEvent.ShowError(
                        UiText.StringResource(R.string.fields_can_t_be_empty)
                    )
                )
                return@launch
            }
            val newDiary = Diary().apply {
                title = state.value.title
                description = state.value.description
                mood = state.value.mood.name
                images = galleryState.images.map { it.remoteImagePath }.toRealmList()
                timestamp = state.value.timestamp
            }

            val result = if (_diary == null) {
                repository.insertDiary(newDiary)
            } else {
                deleteImagesFromFirebase(
                    images = galleryState.imagesToBeDeleted.map { it.remoteImagePath }
                )
                repository.updateDiary(newDiary.apply { _id = _diary!!._id })
            }

            _event.send(
                if (result is Resource.Success) {
                    uploadImageToFirebase()
                    UiEvent.Navigate()
                } else UiEvent.ShowError((result as Resource.Error).uiText)
            )
        }
    }

    fun deleteDiary() {
        viewModelScope.launch {
            if (_diary != null) {
                val result = repository.deleteDiary(_diary?._id!!)
                _event.send(
                    if (result is Resource.Success) {
                        deleteImagesFromFirebase(_diary!!.images.toList())
                        UiEvent.Navigate()
                    } else UiEvent.ShowError((result as Resource.Error).uiText)
                )
            }
        }
    }

    fun addImage(image: Uri, imageType: String) {
        val userIdFirebase = FirebaseAuth.getInstance().currentUser?.uid
        val remoteImagePath =
            "images/$userIdFirebase/${image.lastPathSegment}-${System.currentTimeMillis()}.$imageType"
        galleryState.addImage(
            GalleryImage(
                image = image,
                remoteImagePath = remoteImagePath
            )
        )
    }

    private fun uploadImageToFirebase() {
        val storage = FirebaseStorage.getInstance().reference
        galleryState.images.forEach { galleryImage ->
            val imagePath = storage.child(galleryImage.remoteImagePath)
            imagePath.putFile(galleryImage.image)
                .addOnProgressListener {
                    val sessionUri = it.uploadSessionUri
                    if (sessionUri != null) {
                        viewModelScope.launch {
                            imageToUploadDao.addImageToUpload(
                                ImageToUploadEntity(
                                    remoteImagePath = galleryImage.remoteImagePath,
                                    imageUri = galleryImage.image.toString(),
                                    sessionUri = sessionUri.toString()
                                )
                            )
                        }
                    }
                }
        }
    }

    private fun deleteImagesFromFirebase(images: List<String> = emptyList()) {
        val storage = FirebaseStorage.getInstance().reference
        if (images.isNotEmpty()) {
            images.forEach { remotePath ->
                storage.child(remotePath).delete()
                    .addOnFailureListener {
                        viewModelScope.launch {
                            imageToDeleteDao.addImageToDelete(
                                ImageToDelete(remoteImagePath = remotePath)
                            )
                        }
                    }
            }
        } else {
            galleryState.imagesToBeDeleted.map { it.remoteImagePath }.forEach { remotePath ->
                storage.child(remotePath).delete()
                    .addOnFailureListener {
                        viewModelScope.launch {
                            imageToDeleteDao.addImageToDelete(
                                ImageToDelete(remoteImagePath = remotePath)
                            )
                        }
                    }
            }
        }
    }

    private fun extractImagePath(fullImageUrl: String): String {
        val chunks = fullImageUrl.split("%2F")
        val imageName = chunks[2].split("?").first()
        return "images/${Firebase.auth.currentUser?.uid}/$imageName"
    }
}