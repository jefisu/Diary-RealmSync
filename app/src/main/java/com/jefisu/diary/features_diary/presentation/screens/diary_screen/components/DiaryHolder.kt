package com.jefisu.diary.features_diary.presentation.screens.diary_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.jefisu.diary.features_diary.domain.Diary
import com.jefisu.diary.features_diary.domain.Mood
import com.jefisu.diary.ui.theme.Elevation
import io.realm.kotlin.ext.realmListOf
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryHolder(
    diary: Diary,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var cardHeight by remember { mutableStateOf(0.dp) }
    val mood by remember { mutableStateOf(Mood.valueOf(diary.mood)) }
    var galleryOpened by remember { mutableStateOf(false) }
    var galleryLoading by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.width(14.dp))
        Surface(
            modifier = Modifier
                .width(2.dp)
                .height(cardHeight + 14.dp),
            tonalElevation = Elevation.Level1,
            color = MaterialTheme.colorScheme.onSurface.copy(0.2f)
        ) {}
        Spacer(modifier = Modifier.width(20.dp))
        Card(
            onClick = { onClick(diary._id.toString()) },
            shape = Shapes().medium,
            modifier = Modifier.onGloballyPositioned {
                density.run { cardHeight = it.size.height.toDp() }
            }
        ) {
            DiaryHeader(
                mood = mood,
                timestamp = diary.timestamp,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(mood.containerColor)
                    .padding(vertical = 7.dp, horizontal = 14.dp)
            )
            Column(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
            ) {
                Text(
                    text = diary.description,
                    fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Justify,
                )
                if (diary.images.isNotEmpty()) {
                    TextButton(
                        onClick = { galleryOpened = !galleryOpened },
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                        modifier = Modifier.height(35.dp)
                    ) {
                        Text(
                            text = if (galleryOpened) {
                                if (galleryLoading) "Loading..." else "Hide Gallery"
                            } else "Show Gallery",
                            fontSize = MaterialTheme.typography.bodySmall.fontSize
                        )
                    }
                }
                AnimatedVisibility(visible = galleryOpened) {
                    Gallery(images = diary.images)
                }
            }
        }
    }
}

@Composable
fun DiaryHeader(
    mood: Mood,
    timestamp: Long,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Image(
            modifier = Modifier.size(18.dp),
            painter = painterResource(id = mood.icon),
            contentDescription = "Mood Icon",
        )
        Spacer(modifier = Modifier.width(7.dp))
        Text(
            text = mood.name,
            color = mood.contentColor,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = SimpleDateFormat("hh:mm a", Locale.getDefault())
                .format(timestamp),
            color = mood.contentColor,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize
        )
    }
}

@Composable
fun Gallery(
    images: List<String>,
    modifier: Modifier = Modifier,
    imageSize: Dp = 40.dp,
    spaceBetween: Dp = 10.dp,
    imageShape: Shape = Shapes().small
) {
    BoxWithConstraints(modifier = modifier) {
        val numberOfVisibileImages by remember {
            derivedStateOf {
                max(
                    a = 0,
                    b = this.maxWidth.div(spaceBetween + imageSize).toInt().minus(1)
                )
            }
        }
        val remaingImages by remember {
            derivedStateOf {
                images.size - numberOfVisibileImages
            }
        }
        Row {
            images
                .take(numberOfVisibileImages)
                .forEach { image ->
                    AsyncImage(
                        model = image,
                        contentDescription = "Gallery image",
                        modifier = Modifier
                            .size(imageSize)
                            .clip(imageShape)
                    )
                    Spacer(modifier = Modifier.width(spaceBetween))
                }
            if (remaingImages > 0) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(imageSize)
                        .clip(imageShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(
                        text = "+$remaingImages",
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun DiaryHolderPreview() {
    DiaryHolder(
        diary = Diary().apply {
            title = "My Diary"
            description =
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."
            mood = Mood.Happy.name
            images = realmListOf("", "")
        },
        onClick = {}
    )
}