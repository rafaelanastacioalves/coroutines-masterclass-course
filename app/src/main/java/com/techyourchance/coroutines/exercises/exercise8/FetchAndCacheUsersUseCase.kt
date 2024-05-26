package com.techyourchance.coroutines.exercises.exercise8

import android.util.Log
import com.techyourchance.coroutines.common.ThreadInfoLogger.logThreadInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FetchAndCacheUsersUseCase(
        private val getUserEndpoint: GetUserEndpoint,
        private val usersDao: UsersDao
) {

    suspend fun fetchAndCacheUsers(userIds: List<String>) = withContext(Dispatchers.Default) {
        Log.d("FetchAndCacheUsers", "Fetching users...")
        for (userId in userIds) {
            launch {
                println("")
                Log.d("FetchAndCacheUsers", "Fetching user $userId")
                logThreadInfo("User $userId")
                val user = getUserEndpoint.getUser(userId)
                usersDao.upsertUserInfo(user)
            }
        }
        Log.d("FetchAndCacheUsers", "Users cached!")
    }

}