package com.calsnap.app.domain.repository

import com.calsnap.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUser(): Flow<User?>
    suspend fun saveUser(user: User)
    suspend fun isOnboardingDone(): Boolean
}
