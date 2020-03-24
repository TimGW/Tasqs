package com.timgortworst.roomy.domain.usecase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.IdProvider
import com.timgortworst.roomy.data.repository.TaskRepository
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.presentation.RoomyApp

class UserUseCase(
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val householdRepository: HouseholdRepository,
    private val idProvider: IdProvider
) {

    suspend fun getCurrentUser() =
        userRepository.getUser(FirebaseAuth.getInstance().currentUser?.uid)

    suspend fun getAllUsersForHousehold() =
        userRepository.getAllUsersForHousehold(idProvider.fetchHouseholdId())

    suspend fun getHouseholdIdForUser() = idProvider.fetchHouseholdId()

    suspend fun removeAccount(userId: String?) {
        userId ?: return

        try {
            removeEventsAssignedToUser(userId)

            // delete old household if no other user is left
            val household = idProvider.fetchHouseholdId()
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
