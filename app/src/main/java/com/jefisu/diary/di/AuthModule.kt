package com.jefisu.diary.di

import com.jefisu.diary.features_auth.data.AuthRepositoryImpl
import com.jefisu.diary.features_auth.domain.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import io.realm.kotlin.mongodb.App

@Module
@InstallIn(ViewModelComponent::class)
object AuthModule {

    @Provides
    @ViewModelScoped
    fun provideAuthRepository(app: App): AuthRepository = AuthRepositoryImpl(app)
}