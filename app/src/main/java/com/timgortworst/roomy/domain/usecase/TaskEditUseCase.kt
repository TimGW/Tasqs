package com.timgortworst.roomy.domain.usecase

import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.data.repository.TaskRepository
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.domain.entity.Task
import com.timgortworst.roomy.domain.entity.response.ErrorHandler
import com.timgortworst.roomy.domain.entity.response.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class TaskEditUseCase(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    private val errorHandler: ErrorHandler
) {

    fun createOrUpdateTask(task: Task) = callbackFlow {
        val loadingJob = CoroutineScope(coroutineContext).launch {
            delay(500) // delay 0.5s before showing loading
            offer(Response.Loading)
        }

        try {
            // temporary disable the done button
            val result = task.apply { isDoneEnabled = false }

            if (task.id.isEmpty()) {
                taskRepository.createTask(result)
            } else {
                taskRepository.updateTask(result)
            }
            offer(Response.Success(task))
        } catch (e: FirebaseFirestoreException) {
            offer(Response.Error(errorHandler.getError(e)))
        } finally {
            awaitClose { loadingJob.cancel() }
        }
    }.flowOn(Dispatchers.IO)
}

