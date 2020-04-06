package com.timgortworst.roomy.domain.application.task

import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.domain.repository.TaskRepository
import com.timgortworst.roomy.domain.application.UseCase
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.model.response.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class CreateOrUpdateTaskUseCase(
    private val taskRepository: TaskRepository,
    private val errorHandler: ErrorHandler
) : UseCase<Flow<Response<Task>>> {
    private lateinit var task: Task

    fun init(task: Task): CreateOrUpdateTaskUseCase {
        this.task = task
        return this
    }

    override fun invoke() = callbackFlow {
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

