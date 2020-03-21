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

    suspend fun deleteUser(userId: String?) {
        userId ?: return

        try {
            removeEventsAssignedToUser(userId)

            userRepository.deleteUser(userId)
        } catch (e: FirebaseFirestoreException) {
            // todo update UI with error
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
        val tokens = getCurrentUser()?.registrationTokens ?: return

        if (tokens.contains(token)) return

        tokens.add(token)
        userRepository.updateUser(tokens = tokens)
    }
}
