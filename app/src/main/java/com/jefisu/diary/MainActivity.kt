package com.jefisu.diary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.jefisu.diary.features_auth.presentation.NavGraphs
import com.jefisu.diary.ui.theme.DiaryTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint

@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            DiaryTheme(dynamicColor = false) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    DestinationsNavHost(
                        navGraph = NavGraphs.root,
                        modifier = Modifier
                            .statusBarsPadding()
                            .navigationBarsPadding()
                    )
                }
            }
        }
    }
}