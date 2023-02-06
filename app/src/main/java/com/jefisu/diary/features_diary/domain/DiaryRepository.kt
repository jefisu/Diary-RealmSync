package com.jefisu.diary.features_diary.domain

import com.jefisu.diary.core.util.Resource
import com.jefisu.diary.core.util.SimpleResource
import io.realm.kotlin.types.ObjectId
import kotlinx.coroutines.flow.Flow

typealias Diaries = Resource<List<Diary>>

interface DiaryRepository {
    fun getAllDiaries(): Flow<Diaries>
    fun getDiaryById(id: String): Flow<Resource<Diary>>
    suspend fun insertDiary(diary: Diary): SimpleResource
    suspend fun updateDiary(diary: Diary): SimpleResource
    suspend fun deleteDiary(id: ObjectId): SimpleResource
    suspend fun deleteAllDiaries(): SimpleResource
}