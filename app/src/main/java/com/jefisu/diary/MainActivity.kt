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
import com.jefisu.diary.destinations.AuthScreenDestination
import com.jefisu.diary.destinations.DiaryScreenDestination
import com.jefisu.diary.destinations.DirectionDestination
import com.jefisu.diary.features_diary.presentation.diary_screen.DiaryScreen
import com.jefisu.diary.ui.theme.DiaryTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import dagger.hilt.android.AndroidEntryPoint
import io.realm.kotlin.mongodb.App
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var appRealm: App
    var keepSplashOpened = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition {
            keepSplashOpened
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            DiaryTheme(dynamicColor = false) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    DestinationsNavHost(
                        navGraph = NavGraphs.root,
                        startRoute = getStartDestination(),
                        modifier = Modifier
                            .statusBarsPadding()
                            .navigationBarsPadding(),
                        manualComposableCallsBuilder = {
                            composable(DiaryScreenDestination) {
                                DiaryScreen(
                                    navController = this.navController,
                                    onDataLoaded = {
                                        keepSplashOpened = false
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }
    }

    private fun getStartDestination(): DirectionDestination {
        val user = appRealm.currentUser
        return if (user != null && user.loggedIn) {
            DiaryScreenDestination
        } else {
            keepSplashOpened = false
            AuthScreenDestination
        }
    }
}