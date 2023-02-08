package com.jefisu.diary.di

import com.jefisu.diary.features_diary.data.DiaryRepositoryImpl
import com.jefisu.diary.features_diary.domain.DiaryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import io.realm.kotlin.mongodb.App

@Module
@InstallIn(ViewModelComponent::class)
object DiaryModule {

    @Provides
    @ViewModelScoped
    fun provideDiaryRepository(app: App): DiaryRepository = DiaryRepositoryImpl(app)
}