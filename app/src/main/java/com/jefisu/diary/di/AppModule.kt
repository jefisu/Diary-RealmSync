package com.jefisu.diary.di

import android.app.Application
import androidx.room.Room
import com.jefisu.diary.core.util.getMetaData
import com.jefisu.diary.features_diary.data.database.ImageDatabase
import com.jefisu.diary.features_diary.data.database.ImageToDeleteDao
import com.jefisu.diary.features_diary.data.database.ImageToUploadDao
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

    @Provides
    @Singleton
    fun provideImageDatabase(app: Application): ImageDatabase {
        return Room.databaseBuilder(
            app,
            ImageDatabase::class.java,
            "image_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideImageToUploadDao(db: ImageDatabase): ImageToUploadDao = db.imageToUploadDao

    @Provides
    @Singleton
    fun provideImageToDeletedDao(db: ImageDatabase): ImageToDeleteDao = db.imageToDeleteDao
}