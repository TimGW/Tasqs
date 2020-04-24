package com.timgortworst.roomy.domain.usecase.user

import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.domain.repository.HouseholdRepository
import com.timgortworst.roomy.domain.repository.TaskRepository
import com.timgortworst.roomy.domain.repository.UserRepository
import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.presentation.RoomyApp.Companion.LOADING_DELAY
import com.timgortworst.roomy.presentation.usecase.user.RemoveUserUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class RemoveUserUseCaseImpl(
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val householdRepository: HouseholdRepository,
    private val errorHandler: ErrorHandler
) : RemoveUserUseCase {

    data class Params(val id: String)

    override fun execute(params: Params?) = channelFlow {
        checkNotNull(params)

        val loadingJob = CoroutineScope(coroutineContext).launch {
            delay(LOADING_DELAY)
            offer(Response.Loading)
        }

        try {
            removeEventsAssignedToUser(params.id)
            val householdId = householdRepository.createHousehold()
            userRepository.updateUser(userId = params.id, householdId = householdId, isAdmin = true)

            offer(Response.Success(params.id))
        } catch (e: FirebaseFirestoreException) {
            offer(Response.Error(errorHandler.getError(e)))
        } finally {
            loadingJob.cancel()
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun removeEventsAssignedToUser(userId: String) {
        val tasks = taskRepository.getTasksForUser(userId)
        taskRepository.deleteTasks(tasks)
    }
}
