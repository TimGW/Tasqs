package com.timgortworst.tasqs.domain.usecase.task

import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.tasqs.domain.model.response.ErrorHandler
import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.domain.repository.TaskRepository
import com.timgortworst.tasqs.presentation.usecase.task.GetTaskUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetTaskUseCaseImpl(
    private val taskRepository: TaskRepository,
    private val errorHandler: ErrorHandler
) : GetTaskUseCase {

    data class Params(val id: String)

    override fun execute(params: Params?) = flow {
        checkNotNull(params)
        emit(Response.Loading)

        try {
            val result = taskRepository.getTask(params.id)
            emit(Response.Success(result))
        } catch (e: FirebaseFirestoreException) {
            emit(Response.Error(errorHandler.getError(e)))
        }
    }.flowOn(Dispatchers.IO)
}

