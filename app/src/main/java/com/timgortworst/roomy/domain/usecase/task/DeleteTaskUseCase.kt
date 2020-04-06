package com.timgortworst.roomy.domain.usecase.task

import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.repository.TaskRepository
import com.timgortworst.roomy.domain.usecase.UseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class DeleteTaskUseCase(
    private val taskRepository: TaskRepository,
    private val errorHandler: ErrorHandler
) : UseCase<Flow<Response<Nothing>>> {
    private lateinit var tasks: List<Task>

    fun init(tasks: List<Task>): DeleteTaskUseCase {
        this.tasks = tasks
        return this
    }

    override fun invoke() = flow {
        emit(Response.Loading)
        try {
            taskRepository.deleteTasks(tasks)
            emit(Response.Success())
        } catch (e: FirebaseFirestoreException) {
            emit(Response.Error(errorHandler.getError(e)))
        }
    }.flowOn(Dispatchers.IO)
}

