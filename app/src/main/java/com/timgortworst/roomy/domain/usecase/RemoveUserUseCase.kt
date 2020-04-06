package com.timgortworst.roomy.domain.usecase

import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.TaskRepository
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.domain.UseCase
import com.timgortworst.roomy.domain.entity.User
import com.timgortworst.roomy.domain.entity.response.ErrorHandler
import com.timgortworst.roomy.domain.entity.response.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class RemoveUserUseCase(
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val householdRepository: HouseholdRepository,
    private val errorHandler: ErrorHandler
) : UseCase<Flow<Response<String>>> {
    private lateinit var id: String

    fun init(id: String): RemoveUserUseCase {
        this.id = id
        return this
    }

    override fun invoke() = callbackFlow {
        val loadingJob = CoroutineScope(coroutineContext).launch {
            delay(500) // delay 0.5s before showing loading
            offer(Response.Loading)
        }

        try {
            removeEventsAssignedToUser(id)
            val householdId = householdRepository.createHousehold()
            userRepository.updateUser(userId = id, householdId = householdId, isAdmin = true)

            offer(Response.Success(id))
        } catch (e: FirebaseFirestoreException) {
            offer(Response.Error(errorHandler.getError(e)))
        }  finally {
            awaitClose { loadingJob.cancel() }
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun removeEventsAssignedToUser(userId: String) {
        val tasks = taskRepository.getTasksForUser(userId)
        taskRepository.deleteTasks(tasks)
    }
}
