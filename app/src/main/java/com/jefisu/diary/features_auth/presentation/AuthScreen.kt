package com.jefisu.diary.features_auth.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jefisu.diary.R
import com.jefisu.diary.core.util.UiEvent
import com.jefisu.diary.features_auth.presentation.components.GoogleButton
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import com.stevdzasan.messagebar.ContentWithMessageBar
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.OneTapSignInWithGoogle
import com.stevdzasan.onetap.rememberOneTapSignInState
import kotlinx.coroutines.flow.collect

@ExperimentalMaterial3Api
@RootNavGraph(start = true)
@Destination
@Composable
fun AuthScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val messageBarState = rememberMessageBarState()
    val oneTapState = rememberOneTapSignInState()
    val context = LocalContext.current

    LaunchedEffect(key1 = viewModel.uiEvent) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.Navigate -> {
                    navController.apply {
                        backQueue.clear()
                        navigate(event.direction!!)
                    }
                }
                is UiEvent.ShowError -> {
                    messageBarState.addError(
                        Exception(event.uiText?.asString(context))
                    )
                }
            }
        }
    }

    OneTapSignInWithGoogle(
        state = oneTapState,
        clientId = viewModel.clientId,
        onTokenIdReceived = { tokenId ->
            viewModel.signInWithMongoAtlas(tokenId = tokenId)
        },
        onDialogDismissed = { message ->
            messageBarState.addError(Exception(message))
        }
    )

    ContentWithMessageBar(messageBarState = messageBarState) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Image(
                    modifier = Modifier.size(120.dp),
                    painter = painterResource(id = R.drawable.google_logo),
                    contentDescription = "Google Logo"
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = stringResource(id = R.string.auth_title),
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )
                Text(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                    text = stringResource(id = R.string.auth_subtitle),
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
            }
            GoogleButton(
                isLoading = isLoading,
                onClick = oneTapState::open,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}