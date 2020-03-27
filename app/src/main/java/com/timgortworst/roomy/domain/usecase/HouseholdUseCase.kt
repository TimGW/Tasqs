package com.timgortworst.roomy.domain.usecase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.TaskRepository
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.domain.model.ResponseState

class HouseholdUseCase(
    private val householdRepository: HouseholdRepository,
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository
) {
    suspend fun switchHousehold(newId: String) {
        val oldId = householdRepository.getHouseholdId()
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        try {
            // remove old tasks assigned to user
            taskRepository.deleteTasks(
                taskRepository.getTasksForUser(currentUserId)
            )

            // delete old household if no other user is left to assigned to it
            userRepository.getAllUsersForHousehold(oldId).let {
                if (it.size <= 1) householdRepository.deleteHousehold(oldId)
            }

            // update current user with new household ID and role
            userRepository.updateUser(
                householdId = newId,
                isAdmin = false
            )
        } catch (e: FirebaseFirestoreException) {
            // todo handle errors
        }
    }

    suspend fun currentHouseholdIdForCurrentUser() = householdRepository.getHouseholdId()

    suspend fun isIdSimilarToActiveId(referredHouseholdId: String): Boolean {
        return referredHouseholdId == householdRepository.getHouseholdId()
    }
}
