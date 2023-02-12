package com.jefisu.diary.features_diary.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ImageToDelete(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val remoteImagePath: String
)