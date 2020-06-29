package com.timgortworst.tasqs.domain.usecase.user

import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.tasqs.domain.model.response.ErrorHandler
import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.domain.repository.HouseholdRepository
import com.timgortworst.tasqs.domain.repository.TaskRepository
import com.timgortworst.tasqs.domain.repository.UserRepository
import com.timgortworst.tasqs.presentation.usecase.user.RemoveUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class RemoveUserUseCaseImpl(
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val householdRepository: HouseholdRepository,
    private val errorHandler: ErrorHandler
) : RemoveUserUseCase {

    data class Params(val id: String)

    override fun execute(params: Params?) = flow {
        checkNotNull(params)

        emit(Response.Loading)

        try {
            removeEventsAssignedToUser(params.id)
            val householdId = householdRepository.createHousehold()
            userRepository.updateUser(userId = params.id, householdId = householdId, isAdmin = true)

            emit(Response.Success(params.id))
        } catch (e: FirebaseFirestoreException) {
            emit(Response.Error(errorHandler.getError(e)))
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun removeEventsAssignedToUser(userId: String) {
        val tasks = taskRepository.getTasksForUser(userId)
        taskRepository.deleteTasks(tasks)
    }
}
