package com.timgortworst.roomy.domain.usecase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.repository.TaskRepository
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.IdProvider
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.domain.model.Response

class SetupUseCase(
    private val householdRepository: HouseholdRepository,
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val idProvider: IdProvider
) {

    suspend fun createNewHousehold() = householdRepository.createHousehold()

    suspend fun createNewUser(
        householdId: String,
        fireBaseUser: FirebaseUser
    ) = userRepository.createUser(householdId, fireBaseUser)

    suspend fun switchHousehold(householdId: String, role: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        userRepository.updateUser(householdId = householdId, role = role)

        // remove tasks assigned to user
        taskRepository.getTasksForUser(currentUserId).forEach {
            taskRepository.deleteTask(it.id)
        }
    }

    suspend fun currentHouseholdIdForCurrentUser() = idProvider.getHouseholdId()

    suspend fun userListForCurrentHousehold() = userRepository.getAllUsers()

    suspend fun deleteHousehold(householdId: String) {
        householdRepository.deleteHousehold(householdId)
    }

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
}
