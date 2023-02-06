package com.jefisu.diary.di

import android.app.Application
import com.jefisu.diary.core.util.getMetaData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.mongodb.App
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @Named("APP_ID")
    fun provideAppId(app: Application): String = app.getMetaData("APP_ID")

    @Provides
    @Singleton
    @Named("CLIENT_ID")
    fun provideClientId(app: Application): String = app.getMetaData("CLIENT_ID")

    @Provides
    @Singleton
    fun provideAppRealm(
        @Named("APP_ID") appId: String
    ) = App.create(appId)
}