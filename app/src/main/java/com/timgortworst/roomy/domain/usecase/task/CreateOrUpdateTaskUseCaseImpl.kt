package com.timgortworst.roomy.domain.usecase.task

import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.repository.TaskRepository
import com.timgortworst.roomy.presentation.usecase.CreateOrUpdateTaskUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class CreateOrUpdateTaskUseCaseImpl(
    private val taskRepository: TaskRepository,
    private val errorHandler: ErrorHandler
) : CreateOrUpdateTaskUseCase {

    data class Params(val task: Task)

    override fun execute(params: Params?) = callbackFlow {
        checkNotNull(params)

        val loadingJob = CoroutineScope(coroutineContext).launch {
            delay(500) // delay 0.5s before showing loading
            offer(Response.Loading)
        }

        try {
            // temporary disable the done button
            val result = params.task.apply { isDoneEnabled = false }

            if (params.task.id.isEmpty()) {
                taskRepository.createTask(result)
            } else {
                taskRepository.updateTask(result)
            }
            offer(Response.Success(params.task))
        } catch (e: FirebaseFirestoreException) {
            offer(Response.Error(errorHandler.getError(e)))
        } finally {
            awaitClose { loadingJob.cancel() }
        }
    }.flowOn(Dispatchers.IO)
}

