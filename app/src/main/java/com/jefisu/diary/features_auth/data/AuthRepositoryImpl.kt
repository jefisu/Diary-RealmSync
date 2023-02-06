package com.jefisu.diary.features_auth.data

import com.jefisu.diary.R
import com.jefisu.diary.core.util.Resource
import com.jefisu.diary.core.util.SimpleResource
import com.jefisu.diary.core.util.UiText
import com.jefisu.diary.features_auth.domain.AuthRepository
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials

class AuthRepositoryImpl(
    private val app: App
) : AuthRepository {

    override suspend fun signIn(googleTokenId: String): SimpleResource {
        return try {
            val isLogged = app
                .login(Credentials.jwt(googleTokenId))
                .loggedIn
            if (!isLogged) {
                return Resource.Error(
                    UiText.StringResource(R.string.failed_to_connect_with_your_google_account)
                )
            }
            Resource.Success(Unit)
        } catch (_: Exception) {
            Resource.Error(
                UiText.unknownError()
            )
        }
    }
}