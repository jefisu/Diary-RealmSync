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
import androidx.lifecycle.lifecycleScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.jefisu.diary.core.util.retryDeletingImageFromFirebase
import com.jefisu.diary.core.util.retryUploadingImageToFirebase
import com.jefisu.diary.destinations.AuthScreenDestination
import com.jefisu.diary.destinations.DiaryScreenDestination
import com.jefisu.diary.destinations.DirectionDestination
import com.jefisu.diary.features_diary.data.database.ImageToDeleteDao
import com.jefisu.diary.features_diary.data.database.ImageToUploadDao
import com.jefisu.diary.features_diary.presentation.screens.diary_screen.DiaryScreen
import com.jefisu.diary.ui.theme.DiaryTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.manualcomposablecalls.composable
import dagger.hilt.android.AndroidEntryPoint
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var appRealm: App

    @Inject
    lateinit var imageToUploadDao: ImageToUploadDao

    @Inject
    lateinit var imageToDeleteDao: ImageToDeleteDao
    var keepSplashOpened = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition {
            keepSplashOpened
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        Firebase.initialize(applicationContext)
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
        cleanupCheck(lifecycleScope, imageToUploadDao, imageToDeleteDao)
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

    private fun cleanupCheck(
        scope: CoroutineScope,
        imageToUploadDao: ImageToUploadDao,
        imageToDeleteDao: ImageToDeleteDao
    ) {
        scope.launch {
            val result = imageToUploadDao.getAllImages()
            result.forEach { imageToUpload ->
                retryUploadingImageToFirebase(
                    imageToUpload = imageToUpload,
                    onSuccess = {
                        scope.launch {
                            imageToUploadDao.cleanupImage(imageId = imageToUpload.id)
                        }
                    }
                )
            }
            val result2 = imageToDeleteDao.getAllImages()
            result2.forEach { imageToDelete ->
                retryDeletingImageFromFirebase(
                    imageToDelete = imageToDelete,
                    onSuccess = {
                        scope.launch {
                            imageToDeleteDao.cleanupImage(imageId = imageToDelete.id)
                        }
                    }
                )
            }
        }
    }
}