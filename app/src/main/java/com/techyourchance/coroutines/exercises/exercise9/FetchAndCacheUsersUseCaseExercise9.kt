package com.techyourchance.coroutines.exercises.exercise9

import com.techyourchance.coroutines.exercises.exercise8.GetUserEndpoint
import com.techyourchance.coroutines.exercises.exercise8.User
import com.techyourchance.coroutines.exercises.exercise8.UsersDao
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class FetchAndCacheUsersUseCaseExercise9(
        private val getUserEndpoint: GetUserEndpoint,
        private val usersDao: UsersDao
) {

    suspend fun fetchAndCacheUsers(userIds: List<String>): List<User> = withContext(Dispatchers.Default) {

        val deferredList = mutableListOf<Deferred<User>>()
        for (userId in userIds) {
            val deferredJob = async {
                val user = getUserEndpoint.getUser(userId)
                usersDao.upsertUserInfo(user)
                user
            }
            deferredList.add(deferredJob)
        }

        val finalList: List<User> = deferredList.awaitAll()
//        for (job in deferredList){
//            val user = job.await()
//            finalList.add(user)
//        }
        finalList
    }

}