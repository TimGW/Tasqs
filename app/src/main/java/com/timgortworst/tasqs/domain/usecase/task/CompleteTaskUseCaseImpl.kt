package com.timgortworst.tasqs.domain.usecase.task

import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.tasqs.domain.model.Task
import com.timgortworst.tasqs.domain.model.TaskRecurrence
import com.timgortworst.tasqs.domain.model.response.ErrorHandler
import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.domain.repository.TaskRepository
import com.timgortworst.tasqs.presentation.usecase.task.CalculateNextTaskUseCase
import com.timgortworst.tasqs.presentation.usecase.task.CompleteTaskUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.threeten.bp.ZonedDateTime

class CompleteTaskUseCaseImpl(
    private val taskRepository: TaskRepository,
    private val calcNextTaskDate: CalculateNextTaskUseCase,
    private val errorHandler: ErrorHandler
) : CompleteTaskUseCase {

    data class Params(val tasks: List<Task>)

    override fun execute(params: Params?) = flow {
        checkNotNull(params)

        try {
            val (singleTasks, restTasks) = params.tasks.partition {
                it.metaData!!.recurrence is TaskRecurrence.SingleTask
            }

            // delete single tasks
            taskRepository.deleteTasks(singleTasks)

            // update repeating tasks
            restTasks.forEach { task ->
                calcNextTaskDate(task.metaData!!).collect {
                    when (it) {
                        is Response.Success -> task.metaData!!.startDateTime = it.data!!
                        is Response.Error -> emit(Response.Error(it.error))
                    }
                }
            }
            if(restTasks.isNotEmpty()) taskRepository.updateTasks(restTasks)

            emit(Response.Success<Nothing>())
        } catch (e: FirebaseFirestoreException) {
            emit(Response.Error(errorHandler.getError(e)))
        }
    }.flowOn(Dispatchers.IO)

    private fun calcNextTaskDate(taskMetaData: Task.MetaData): Flow<Response<ZonedDateTime>> {
        val params = CalculateNextTaskUseCaseImpl.Params(taskMetaData.startDateTime!!, taskMetaData.recurrence!!)
        return calcNextTaskDate.execute(params)
    }
}

