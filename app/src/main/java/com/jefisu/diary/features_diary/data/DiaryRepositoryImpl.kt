package com.jefisu.diary.features_diary.data

import com.jefisu.diary.R
import com.jefisu.diary.core.util.Resource
import com.jefisu.diary.core.util.UiText
import com.jefisu.diary.features_diary.domain.Diaries
import com.jefisu.diary.features_diary.domain.Diary
import com.jefisu.diary.features_diary.domain.DiaryRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.query.Sort
import io.realm.kotlin.types.ObjectId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class DiaryRepositoryImpl(
    app: App
) : DiaryRepository {

    private val user = app.currentUser
    private lateinit var realm: Realm

    init {
        setUpRealm()
    }

    override fun setUpRealm() {
        if (user != null) {
            val config = SyncConfiguration.Builder(user, setOf(Diary::class))
                .initialSubscriptions { sub ->
                    add(
                        query = sub.query<Diary>("ownerId == $0", user.id),
                        name = "User's Diaries"
                    )
                }
                .log(LogLevel.ALL)
                .build()
            realm = Realm.open(config)
            CoroutineScope(Dispatchers.Main).launch {
                realm.subscriptions.waitForSynchronization()
            }
        }
    }

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

    override fun getDiaryById(id: String): Resource<Diary> {
        return try {
            val response = realm
                .query<Diary>("_id == $0", ObjectId.from(id))
                .find()
                .firstOrNull() ?: return Resource.Error(
                UiText.DynamicString("Record not found")
            )
            Resource.Success(response)
        } catch (_: Exception) {
            Resource.Error(
                UiText.StringResource(R.string.it_was_not_possible_to_load_the_data)
            )
        }
    }
}