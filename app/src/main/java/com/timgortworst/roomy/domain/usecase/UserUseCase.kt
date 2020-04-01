package com.timgortworst.roomy.domain.usecase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.TaskRepository
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.model.response.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class UserUseCase(
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val householdRepository: HouseholdRepository,
    private val errorHandler: ErrorHandler
) {

    suspend fun getCurrentUser() =
        userRepository.getUser(FirebaseAuth.getInstance().currentUser?.uid)

    fun getAllUsersForHousehold() = flow {
        emit(Response.Loading)
        try {
            emit(Response.Success(userRepository.getAllUsersForHousehold(householdRepository.getHouseholdId())))
        } catch (e: FirebaseFirestoreException) {
            emit(Response.Error(errorHandler.getError(e)))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getHouseholdIdForUser() = householdRepository.getHouseholdId()

    fun removeUserFromHousehold(userId: String?) = flow {
        emit(Response.Loading)

        userId ?: run { emit(Response.Error()); return@flow }

        try {
            removeEventsAssignedToUser(userId)

            val householdId = householdRepository.createHousehold()

            userRepository.updateUser(userId = userId, householdId = householdId, isAdmin = true)

            emit(Response.Success())
        } catch (e: FirebaseFirestoreException) {
            emit(Response.Error(errorHandler.getError(e)))
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun removeEventsAssignedToUser(userId: String) {
        val tasks = taskRepository.getTasksForUser(userId)
        taskRepository.deleteTasks(tasks)
    }

    suspend fun addTokenToUser(token: String) {
        userRepository.addUserToken(token = token)
    }
}
