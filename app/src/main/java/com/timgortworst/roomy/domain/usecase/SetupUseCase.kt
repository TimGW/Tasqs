package com.timgortworst.roomy.domain.usecase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.IdProvider
import com.timgortworst.roomy.data.repository.TaskRepository
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.domain.model.ResponseState

class SetupUseCase(
    private val householdRepository: HouseholdRepository,
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val idProvider: IdProvider
) {
    suspend fun switchHousehold(newId: String) {
        val oldId = idProvider.fetchHouseholdId()
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

    suspend fun currentHouseholdIdForCurrentUser() = idProvider.fetchHouseholdId()

    suspend fun isIdSimilarToActiveId(referredHouseholdId: String): Boolean {
        return referredHouseholdId == idProvider.fetchHouseholdId()
    }

    suspend fun handleLoginResult(
        fbUser: FirebaseUser?,
        newUser: Boolean,
        registrationToken: String
    ): ResponseState {
        val fireBaseUser = fbUser ?: return ResponseState.Error(R.string.error_generic)

        return try {
            if (newUser) {
                val householdId = householdRepository.createHousehold()
                userRepository.createUser(householdId, fireBaseUser, registrationToken)
            } else {
                userRepository.addUserToken(fireBaseUser.uid, registrationToken)
            }
            ResponseState.Success(fbUser.displayName)
        } catch (e: FirebaseFirestoreException) {
            ResponseState.Error(R.string.error_generic)
        }
    }
}
