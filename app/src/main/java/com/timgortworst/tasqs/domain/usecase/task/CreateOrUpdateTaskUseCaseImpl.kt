package com.timgortworst.tasqs.domain.usecase.task

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.tasqs.domain.model.Task
import com.timgortworst.tasqs.domain.model.response.ErrorHandler
import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.domain.repository.TaskRepository
import com.timgortworst.tasqs.presentation.usecase.task.CreateOrUpdateTaskUseCase
import com.timgortworst.tasqs.presentation.usecase.task.SetNotificationUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class CreateOrUpdateTaskUseCaseImpl(
    private val taskRepository: TaskRepository,
    private val errorHandler: ErrorHandler,
    private val setNotificationUseCase: SetNotificationUseCase
) : CreateOrUpdateTaskUseCase {

    data class Params(val task: Task)

    override fun execute(params: Params) = flow {
        emit(Response.Loading)

        try {
            val result = params.task
            if (params.task.id.isNullOrBlank()) {
                val newId = taskRepository.createTask(result)
                result.apply { id = newId }
            } else {
                taskRepository.updateTask(result)
            }

            setNotificationUseCase.execute(SetNotificationUseCaseImpl.Params(result)).collect()

            emit(Response.Success(params.task))
        } catch (e: FirebaseFirestoreException) {
            emit(Response.Error(errorHandler.getError(e)))
        }
    }.flowOn(Dispatchers.IO)
}

