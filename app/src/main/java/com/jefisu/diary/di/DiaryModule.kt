package com.jefisu.diary.di

import com.jefisu.diary.features_diary.data.DiaryRepositoryImpl
import com.jefisu.diary.features_diary.domain.Diary
import com.jefisu.diary.features_diary.domain.DiaryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Module
@InstallIn(ViewModelComponent::class)
object DiaryModule {

    @Provides
    @ViewModelScoped
    fun provideRealmMongoDB(app: App): Realm {
        val user = app.currentUser!!
        val config = SyncConfiguration.Builder(user, setOf(Diary::class))
            .initialSubscriptions { sub ->
                add(
                    query = sub.query<Diary>("ownerId == $0", user.id),
                    name = "User's Diaries"
                )
            }
            .log(LogLevel.ALL)
            .build()

        return Realm.open(config).apply {
            CoroutineScope(Dispatchers.Main).launch {
                subscriptions.waitForSynchronization()
            }
        }
    }

    @Provides
    @ViewModelScoped
    fun provideDiaryRepository(app: App, realm: Realm): DiaryRepository {
        return DiaryRepositoryImpl(app, realm)
    }
}