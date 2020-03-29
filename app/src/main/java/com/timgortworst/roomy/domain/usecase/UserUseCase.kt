package com.timgortworst.roomy.domain.usecase

import androidx.lifecycle.liveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.TaskRepository
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.domain.model.Response
import com.timgortworst.roomy.domain.model.TaskUser
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class UserUseCase(
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val householdRepository: HouseholdRepository
) {

    suspend fun getCurrentUser() =
        userRepository.getUser(FirebaseAuth.getInstance().currentUser?.uid)

    suspend fun getCurrentTaskUser(): TaskUser? {
        val result = userRepository.getUser(FirebaseAuth.getInstance().currentUser?.uid) ?: return null
        return TaskUser(result.userId, result.name)
    }

    fun getAllUsersForHousehold() = flow {
        emit(Response.Loading)
        try {
            emit(Response.Success(userRepository.getAllUsersForHousehold(householdRepository.getHouseholdId())))
        } catch (e: FirebaseFirestoreException) {
            emit(Response.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getAllTaskUsers(): List<TaskUser> {
        val result = userRepository.getAllUsersForHousehold(householdRepository.getHouseholdId())
        return result.map { TaskUser(it.userId, it.name) }
    }

    suspend fun getHouseholdIdForUser() = householdRepository.getHouseholdId()

//    suspend fun removeAccount(userId: String?) {
//        userId ?: return
//
//        try {
//            removeEventsAssignedToUser(userId)
//
//            // delete old household if no other user is left
//            val household = householdRepository.getHouseholdId()
//            userRepository.getAllUsersForHousehold(household).let {
//                if (it.size <= 1) householdRepository.deleteHousehold(household)
//            }
//
//            // remove user data
//            userRepository.deleteUser(userId)
//        } catch (e: FirebaseFirestoreException) {
//            // todo handle errors
//            Log.e(RoomyApp.TAG, e.localizedMessage.orEmpty())
//        }
//    }

    suspend fun removeUserFromHousehold(userId: String?) {
        userId ?: return

        try {
            removeEventsAssignedToUser(userId)

            val householdId = householdRepository.createHousehold()

            userRepository.updateUser(userId = userId, householdId = householdId, isAdmin = true)

        } catch (e: FirebaseFirestoreException) {
            // todo update UI with error
//            Log.e(RoomyApp.TAG, e.localizedMessage.orEmpty())
        }
    }

    private suspend fun removeEventsAssignedToUser(userId: String) {
        val tasks = taskRepository.getTasksForUser(userId)
        taskRepository.deleteTasks(tasks)
    }

    suspend fun addTokenToUser(token: String) {
        userRepository.addUserToken(token = token)
    }
}
