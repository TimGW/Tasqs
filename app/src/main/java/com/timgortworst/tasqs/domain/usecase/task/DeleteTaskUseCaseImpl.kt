package com.timgortworst.tasqs.domain.usecase.task

import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.tasqs.domain.model.Task
import com.timgortworst.tasqs.domain.model.response.ErrorHandler
import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.domain.repository.TaskRepository
import com.timgortworst.tasqs.presentation.usecase.task.DeleteTaskUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class DeleteTaskUseCaseImpl(
    private val taskRepository: TaskRepository,
    private val errorHandler: ErrorHandler
) : DeleteTaskUseCase {

    data class Params(val tasks: List<Task>)

    override fun execute(params: Params?) = flow {
        checkNotNull(params)

        emit(Response.Loading)

        try {
            taskRepository.deleteTasks(params.tasks)
            emit(Response.Success())
        } catch (e: FirebaseFirestoreException) {
            emit(Response.Error(errorHandler.getError(e)))
        }
    }.flowOn(Dispatchers.IO)
}

