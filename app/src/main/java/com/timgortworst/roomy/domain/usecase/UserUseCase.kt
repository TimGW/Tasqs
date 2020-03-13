package com.timgortworst.roomy.domain.usecase

import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.roomy.data.repository.TaskRepository
import com.timgortworst.roomy.data.repository.IdProvider
import com.timgortworst.roomy.data.repository.UserRepository

class UserUseCase(
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val idProvider: IdProvider
) {

    suspend fun getCurrentUser() =
        userRepository.getUser(FirebaseAuth.getInstance().currentUser?.uid)

    suspend fun getAllUsersForHousehold() =
        userRepository.getAllUsersForHousehold(idProvider.getHouseholdId())

    suspend fun getHouseholdIdForUser() = idProvider.getHouseholdId()

    suspend fun deleteUser(userId: String?) {
        userId ?: return
        removeEventsAssignedToUser(userId)
        userRepository.deleteUser(userId)
    }

    private suspend fun removeEventsAssignedToUser(userId: String) {
        val tasks = taskRepository.getTasksForUser(userId)
        taskRepository.deleteTasks(tasks)
    }

    //    private suspend fun addUserToBlackList(userId: String) {
//        val household = householdRepository.getHousehold(userRepository.getHouseholdIdForUser(userId))
//        household?.userIdBlackList?.add(userId)
//        householdRepository.updateHousehold(household?.householdId, household?.userIdBlackList)
//    }
}
