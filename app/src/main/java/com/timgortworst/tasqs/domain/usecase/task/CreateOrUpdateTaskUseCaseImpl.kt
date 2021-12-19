package com.timgortworst.tasqs.domain.usecase.task

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.tasqs.domain.model.Task
import com.timgortworst.tasqs.domain.model.response.ErrorHandler
import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.domain.repository.TaskRepository
import com.timgortworst.tasqs.domain.usecase.user.GetUserUseCaseImpl
import com.timgortworst.tasqs.presentation.usecase.task.CreateOrUpdateTaskUseCase
import com.timgortworst.tasqs.presentation.usecase.task.SetNotificationUseCase
import com.timgortworst.tasqs.presentation.usecase.user.GetUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class CreateOrUpdateTaskUseCaseImpl(
    private val taskRepository: TaskRepository,
    private val errorHandler: ErrorHandler,
    private val setNotificationUseCase: SetNotificationUseCase,
    private val getUserUseCase: GetUserUseCase
) : CreateOrUpdateTaskUseCase {

    data class Params(val task: Task)

    override fun execute(params: Params) = flow {
        emit(Response.Loading)

        val result = params.task
        val userId = params.task.user?.userId ?: FirebaseAuth.getInstance().currentUser?.uid

        getUserUseCase.execute(GetUserUseCaseImpl.Params(userId = userId)).collect {
            val data = (it as? Response.Success)?.data ?: return@collect
            result.user = Task.User(data.userId, data.name)
        }

        try {

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

