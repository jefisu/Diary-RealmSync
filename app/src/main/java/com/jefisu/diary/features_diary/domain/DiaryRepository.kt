package com.jefisu.diary.features_diary.domain

import com.jefisu.diary.core.util.Resource
import kotlinx.coroutines.flow.Flow

typealias Diaries = Resource<List<Diary>>

interface DiaryRepository {
    fun setUpRealm()
    fun getAllDiaries(): Flow<Diaries>
    fun getDiaryById(id: String): Flow<Resource<Diary>>
    suspend fun insertDiary(diary: Diary): Resource<Diary>
    suspend fun updateDiary(diary: Diary): Resource<Diary>
}