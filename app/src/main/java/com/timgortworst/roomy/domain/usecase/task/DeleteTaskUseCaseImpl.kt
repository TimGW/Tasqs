package com.timgortworst.roomy.domain.usecase.task

import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.repository.TaskRepository
import com.timgortworst.roomy.presentation.RoomyApp
import com.timgortworst.roomy.presentation.RoomyApp.Companion.LOADING_DELAY
import com.timgortworst.roomy.presentation.usecase.DeleteTaskUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class DeleteTaskUseCaseImpl(
    private val taskRepository: TaskRepository,
    private val errorHandler: ErrorHandler
) : DeleteTaskUseCase {

    data class Params(val tasks: List<Task>)

    override fun execute(params: Params?) = flow {
        checkNotNull(params)

        val loadingJob = CoroutineScope(coroutineContext).launch {
            delay(LOADING_DELAY)
            emit(Response.Loading)
        }

        try {
            taskRepository.deleteTasks(params.tasks)
            emit(Response.Success())
        } catch (e: FirebaseFirestoreException) {
            emit(Response.Error(errorHandler.getError(e)))
        } finally {
            loadingJob.cancel()
        }
    }.flowOn(Dispatchers.IO)
}

