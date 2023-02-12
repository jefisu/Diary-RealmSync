package com.jefisu.diary.core.util

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storageMetadata
import com.jefisu.diary.R
import com.jefisu.diary.features_diary.data.database.entity.ImageToDelete
import com.jefisu.diary.features_diary.data.database.entity.ImageToUploadEntity

/**
 * Download images from Firebase asynchronously.
 * This function returns imageUri after each successful download.
 * */
fun fetchImagesFromFirebase(
    remoteImagePaths: List<String>,
    onResultDownload: (Resource<Uri>) -> Unit,
    onReadyToDisplay: () -> Unit = {}
) {
    if (remoteImagePaths.isEmpty()) {
        return
    }
    remoteImagePaths.forEachIndexed { index, remoteImagePath ->
        if (remoteImagePath.trim().isNotEmpty()) {
            FirebaseStorage.getInstance().reference.child(remoteImagePath.trim()).downloadUrl
                .addOnSuccessListener {
                    Log.d("DownloadURL", "$it")
                    onResultDownload(
                        Resource.Success(it)
                    )
                    if (remoteImagePaths.lastIndexOf(remoteImagePaths.last()) == index) {
                        onReadyToDisplay()
                    }
                }.addOnFailureListener {
                    onResultDownload(
                        Resource.Error(
                            UiText.StringResource(R.string.images_not_uploaded_yet_wait_a_little_bit_or_try_uploading_again)
                        )
                    )
                }
        }
    }
}

fun retryUploadingImageToFirebase(
    imageToUpload: ImageToUploadEntity,
    onSuccess: () -> Unit
) {
    val storage = FirebaseStorage.getInstance().reference
    storage.child(imageToUpload.remoteImagePath).putFile(
        imageToUpload.imageUri.toUri(),
        storageMetadata { },
        imageToUpload.sessionUri.toUri()
    ).addOnSuccessListener { onSuccess() }
}

fun retryDeletingImageFromFirebase(
    imageToDelete: ImageToDelete,
    onSuccess: () -> Unit
) {
    val storage = FirebaseStorage.getInstance().reference
    storage.child(imageToDelete.remoteImagePath).delete()
        .addOnSuccessListener { onSuccess() }
}