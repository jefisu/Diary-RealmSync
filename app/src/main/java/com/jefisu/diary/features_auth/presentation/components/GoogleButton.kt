package com.jefisu.diary.features_auth.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jefisu.diary.R

@Composable
fun GoogleButton(
    onClick: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    primaryText: String = stringResource(R.string.sign_in_with_google),
    secondaryText: String = stringResource(R.string.please_wait),
    icon: Int = R.drawable.google_logo,
    shape: Shape = Shapes().small,
    borderColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderStrokeWidth: Dp = 1.5.dp,
    progressIndicatorColor: Color = MaterialTheme.colorScheme.primary
) {
    var buttonText by remember { mutableStateOf(primaryText) }

    LaunchedEffect(key1 = isLoading) {
        buttonText = if (isLoading) secondaryText else primaryText
    }

    Button(
        onClick = { if (!isLoading) onClick() },
        shape = shape,
        contentPadding = PaddingValues(12.dp),
        border = BorderStroke(
            width = borderStrokeWidth,
            color = borderColor
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black
        ),
        modifier = Modifier
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            )
            .then(modifier)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = "Google Logo",
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = buttonText,
            style = MaterialTheme.typography.bodyMedium
        )
        if (isLoading) {
            Spacer(modifier = Modifier.width(16.dp))
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                color = progressIndicatorColor,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Preview
@Composable
fun PreviewGoogleButton() {
    GoogleButton(
        onClick = { },
        isLoading = false
    )
}