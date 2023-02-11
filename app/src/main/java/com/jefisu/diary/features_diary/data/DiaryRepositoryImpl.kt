package com.jefisu.diary.features_diary.data

import com.jefisu.diary.R
import com.jefisu.diary.core.util.Resource
import com.jefisu.diary.core.util.SimpleResource
import com.jefisu.diary.core.util.UiText
import com.jefisu.diary.features_diary.domain.Diaries
import com.jefisu.diary.features_diary.domain.Diary
import com.jefisu.diary.features_diary.domain.DiaryRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.query.Sort
import io.realm.kotlin.types.ObjectId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class DiaryRepositoryImpl(
    app: App,
    private val realm: Realm
) : DiaryRepository {

    private val user = app.currentUser

    override fun getAllDiaries(): Flow<Diaries> {
        return try {
            realm.query<Diary>("ownerId == $0", user!!.id)
                .sort(property = "timestamp", sortOrder = Sort.DESCENDING)
                .asFlow()
                .map { result ->
                    Resource.Success(result.list)
                }
        } catch (e: Exception) {
            flowOf(
                Resource.Error(UiText.StringResource(R.string.it_was_not_possible_to_load_the_data))
            )
        }
    }

    override fun getDiaryById(id: String): Flow<Resource<Diary>> {
        return try {
            realm
                .query<Diary>("_id == $0", ObjectId.from(id))
                .asFlow()
                .map {
                    Resource.Success(it.list.firstOrNull())
                }
        } catch (_: Exception) {
            flowOf(
                Resource.Error(
                    UiText.StringResource(R.string.it_was_not_possible_to_load_the_data)
                )
            )
        }
    }

    override suspend fun insertDiary(diary: Diary): SimpleResource {
        return realm.write {
            try {
                copyToRealm(
                    diary.apply { ownerId = user!!.id }
                )
                Resource.Success(Unit)
            } catch (_: Exception) {
                Resource.Error(
                    UiText.StringResource(R.string.it_was_not_possible_to_insert_try_again_later)
                )
            }
        }
    }

    override suspend fun updateDiary(diary: Diary): SimpleResource {
        return realm.write {
            val queriedDiary = query<Diary>("_id == $0", diary._id).first().find()
                ?: return@write Resource.Error(UiText.DynamicString("Queried Diary not exist."))
            try {
                queriedDiary.apply {
                    title = diary.title
                    description = diary.description
                    mood = diary.mood
                    images = diary.images
                    timestamp = diary.timestamp
                }
                Resource.Success(Unit)
            } catch (_: Exception) {
                Resource.Error(
                    UiText.StringResource(
                        R.string.it_was_not_possible_to_insert_try_again_later,
                        "insert"
                    )
                )
            }
        }
    }

    override suspend fun deleteDiary(id: ObjectId): SimpleResource {
        return realm.write {
            val diary =
                query<Diary>("_id == $0 AND ownerId == $1", id, user!!.id)
                    .find().firstOrNull()
                    ?: return@write Resource.Error(UiText.DynamicString("Queried Diary not exist."))
            try {
                this.delete(diary)
                Resource.Success(Unit)
            } catch (_: Exception) {
                Resource.Error(
                    UiText.StringResource(
                        R.string.it_was_not_possible_to_insert_try_again_later,
                        "delete"
                    )
                )
            }
        }
    }
}