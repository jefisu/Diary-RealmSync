package com.jefisu.diary.features_diary.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jefisu.diary.features_diary.data.database.entity.ImageToUploadEntity

@Dao
interface ImageToUploadDao {
    @Query("SELECT * FROM ImageToUploadEntity ORDER BY id ASC")
    suspend fun getAllImages(): List<ImageToUploadEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addImageToUpload(imageToUpload: ImageToUploadEntity)

    @Query("DELETE FROM ImageToUploadEntity WHERE id=:imageId")
    suspend fun cleanupImage(imageId: Int)
}