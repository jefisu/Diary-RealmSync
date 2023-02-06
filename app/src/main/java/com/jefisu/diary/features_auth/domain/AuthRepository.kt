package com.jefisu.diary.features_auth.domain

import com.jefisu.diary.core.util.SimpleResource

interface AuthRepository {
    suspend fun signIn(googleTokenId: String): SimpleResource
}