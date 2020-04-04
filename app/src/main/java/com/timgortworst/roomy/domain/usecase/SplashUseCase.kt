package com.timgortworst.roomy.domain.usecase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.TaskRepository
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.model.response.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class SplashUseCase(
    private val householdRepository: HouseholdRepository,
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val errorHandler: ErrorHandler
) {
    fun switchHousehold(newId: String) = flow {
        emit(Response.Loading)
        try {
            val oldId = userRepository.getUser()?.householdId ?: return@flow
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@flow

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
            emit(Response.Success())
        } catch (e: FirebaseFirestoreException) {
            emit(Response.Error(errorHandler.getError(e)))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun fetchHouseholdId()= userRepository.getUser()?.householdId.orEmpty()

    suspend fun isIdSimilarToActiveId(referredHouseholdId: String): Boolean {
        return referredHouseholdId == userRepository.getUser()?.householdId
    }
}
