package com.timgortworst.roomy.domain.usecase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.repository.TaskRepository
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.IdProvider
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.domain.model.Response
import com.timgortworst.roomy.domain.model.Role

class SetupUseCase(
    private val householdRepository: HouseholdRepository,
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val idProvider: IdProvider
) {
    suspend fun switchHousehold(newId: String) {
        val oldId= idProvider.getHouseholdId()
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // update user with new household ID and role
        userRepository.updateUser(
            userId = currentUserId,
            householdId = newId,
            role = Role.NORMAL.name
        )

        // remove old tasks assigned to user
        val tasks= taskRepository.getTasksForUser(currentUserId)
        taskRepository.deleteTasks(tasks)

        // delete old household if no user is assigned to it
        userRepository.getAllUsersForHousehold(oldId)?.let {
            if (it.isEmpty()) {
                householdRepository.deleteHousehold(oldId)
            }
        }
    }

    suspend fun currentHouseholdIdForCurrentUser() = idProvider.getHouseholdId()

    suspend fun isIdSimilarToActiveId(referredHouseholdId: String): Boolean {
        return referredHouseholdId == idProvider.getHouseholdId()
    }

    suspend fun handleLoginResult(
        fbUser: FirebaseUser?,
        newUser: Boolean
    ): Response {
        val user = fbUser ?: return Response.Error(R.string.error_generic)

        if (newUser) {
            val householdId = createNewHousehold() ?: run {
                return Response.Error(R.string.error_generic)
            }
            createNewUser(householdId, user)
        }
        return Response.Success<Nothing>()
    }

    private suspend fun createNewHousehold() = householdRepository.createHousehold()

    private suspend fun createNewUser(
        householdId: String,
        fireBaseUser: FirebaseUser
    ) = userRepository.createUser(householdId, fireBaseUser)
}
