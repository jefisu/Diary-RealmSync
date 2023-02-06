package com.jefisu.diary.features_diary.presentation.screens.add_edit_screen.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransparentTextField(
    text: String,
    onTextChange: (String) -> Unit,
    hint: String,
    keyboardActions: KeyboardActions,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier = Modifier,
    isUniqueLine: Boolean = false,
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(
        containerColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        placeholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
    )
) {
    TextField(
        value = text,
        onValueChange = onTextChange,
        placeholder = { Text(text = hint) },
        colors = colors,
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        singleLine = isUniqueLine,
        modifier = modifier
    )
}