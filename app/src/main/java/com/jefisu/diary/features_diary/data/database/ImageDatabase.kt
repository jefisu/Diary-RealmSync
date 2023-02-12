package com.jefisu.diary.features_diary.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jefisu.diary.features_diary.data.database.entity.ImageToUploadEntity

@Database(entities = [ImageToUploadEntity::class], version = 1, exportSchema = false)
abstract class ImageDatabase : RoomDatabase() {
    abstract val dao: ImageToUploadDao
}