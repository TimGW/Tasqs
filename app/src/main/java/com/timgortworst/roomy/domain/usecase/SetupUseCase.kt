package com.timgortworst.roomy.domain.usecase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.timgortworst.roomy.data.repository.TaskRepository
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.IdProvider
import com.timgortworst.roomy.data.repository.UserRepository

class SetupUseCase(private val householdRepository: HouseholdRepository,
                   private val userRepository: UserRepository,
                   private val taskRepository: TaskRepository,
                   private val idProvider: IdProvider) {

    suspend fun initializeHousehold(fireBaseUser: FirebaseUser): String? {
        val householdId = householdRepository.createHousehold() ?: return null
        userRepository.createUser(householdId, fireBaseUser)
        return householdId
    }

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

//    suspend fun userBlackListedForHousehold(householdId: String): Boolean {
//        val household = householdRepository.getHousehold(householdId)
//        return household?.userIdBlackList?.contains(uId.orEmpty()) ?: false
//    }

//    suspend fun isHouseholdFull(referredHouseholdId: String): Boolean {
//        val userList = userRepository.getUserListForHousehold(referredHouseholdId) ?: return true
//        return userList.size >= 10
//    }

    suspend fun isIdSimilarToActiveId(referredHouseholdId: String): Boolean {
        return referredHouseholdId == idProvider.getHouseholdId()
    }
}
