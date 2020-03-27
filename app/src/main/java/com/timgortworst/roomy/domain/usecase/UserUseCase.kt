package com.timgortworst.roomy.domain.usecase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.TaskRepository
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.domain.model.TaskUser
import com.timgortworst.roomy.presentation.RoomyApp

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

    suspend fun getAllUsersForHousehold() =
        userRepository.getAllUsersForHousehold(householdRepository.getHouseholdId())

    suspend fun getHouseholdIdForUser() = householdRepository.getHouseholdId()

    suspend fun removeAccount(userId: String?) {
        userId ?: return

        try {
            removeEventsAssignedToUser(userId)

            // delete old household if no other user is left
            val household = householdRepository.getHouseholdId()
            userRepository.getAllUsersForHousehold(household).let {
                if (it.size <= 1) householdRepository.deleteHousehold(household)
            }

            // remove user data
            userRepository.deleteUser(userId)
        } catch (e: FirebaseFirestoreException) {
            // todo handle errors
            Log.e(RoomyApp.TAG, e.localizedMessage.orEmpty())
        }
    }

    suspend fun removeUserFromHousehold(userId: String?) {
        userId ?: return

        try {
            removeEventsAssignedToUser(userId)

            val householdId = householdRepository.createHousehold()

            userRepository.updateUser(userId = userId, householdId = householdId, isAdmin = true)

        } catch (e: FirebaseFirestoreException) {
            // todo update UI with error
            Log.e(RoomyApp.TAG, e.localizedMessage.orEmpty())
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
