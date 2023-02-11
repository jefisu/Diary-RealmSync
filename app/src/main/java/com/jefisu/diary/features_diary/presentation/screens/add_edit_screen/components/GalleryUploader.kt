package com.jefisu.diary.features_diary.presentation.screens.add_edit_screen.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.jefisu.diary.features_diary.domain.GalleryImage
import com.jefisu.diary.features_diary.domain.GalleryState

@Composable
fun GalleryUploader(
    galleryState: GalleryState,
    modifier: Modifier = Modifier,
    imageSize: Dp = 60.dp,
    imageShape: Shape = Shapes().medium,
    spaceBetween: Dp = 12.dp,
    onAddClicked: () -> Unit,
    onImageSelect: (Uri) -> Unit,
    onImageClicked: (GalleryImage) -> Unit
) {
    val multiplePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 8),
        onResult = { images ->
            images.forEach(onImageSelect::invoke)
        }
    )
    BoxWithConstraints(modifier = modifier) {
        val numberOfVisibleImages by remember {
            derivedStateOf {
                (this.maxWidth / (spaceBetween + imageSize)).toInt() - 1
            }
        }
        val remainingImages by remember {
            derivedStateOf {
                galleryState.images.size - numberOfVisibleImages
            }
        }
        Row {
            FilledTonalIconButton(
                modifier = Modifier.size(imageSize),
                shape = imageShape,
                onClick = {
                    onAddClicked()
                    multiplePhotoPicker.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add icon"
                )
            }
            Spacer(modifier = Modifier.width(spaceBetween))
            galleryState.images
                .take(numberOfVisibleImages)
                .forEach { galleryImage ->
                    AsyncImage(
                        model = galleryImage.image,
                        contentDescription = "Gallery image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(imageSize)
                            .clip(imageShape)
                            .clickable { onImageClicked(galleryImage) }
                    )
                    Spacer(modifier = Modifier.width(spaceBetween))
                }
            if (remainingImages > 0) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(imageSize)
                        .clip(imageShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(
                        text = "+$remainingImages",
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}