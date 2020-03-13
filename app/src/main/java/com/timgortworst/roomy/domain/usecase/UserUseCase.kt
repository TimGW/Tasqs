package com.timgortworst.roomy.domain.usecase

import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.roomy.data.repository.TaskRepository
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.IdProvider
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.domain.utils.InviteLinkBuilder

class UserUseCase(
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val idProvider: IdProvider
) {

    suspend fun getCurrentUser() =
        userRepository.getUser(FirebaseAuth.getInstance().currentUser?.uid)

    suspend fun getAllUsersForHousehold() = userRepository.getAllUsersForHousehold(idProvider.getHouseholdId())

    suspend fun getHouseholdIdForUser() = idProvider.getHouseholdId()

    suspend fun deleteFirestoreData() {
        val id = getCurrentUser()?.userId ?: return
        removeEventsAssignedToUser(id)
        userRepository.deleteUser(id)
    }

    private suspend fun removeEventsAssignedToUser(userId: String) {
        val tasks = taskRepository.getTasksForUser(userId)
        taskRepository.deleteTasks(tasks)
    }

    suspend fun deleteUser(id: String) = userRepository.deleteUser(id)


    //    private suspend fun addUserToBlackList(userId: String) {
//        val household = householdRepository.getHousehold(userRepository.getHouseholdIdForUser(userId))
//        household?.userIdBlackList?.add(userId)
//        householdRepository.updateHousehold(household?.householdId, household?.userIdBlackList)
//    }
}
