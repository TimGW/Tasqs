package com.timgortworst.tasqs.domain.usecase.task

import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.tasqs.domain.model.Task
import com.timgortworst.tasqs.domain.model.TaskRecurrence
import com.timgortworst.tasqs.domain.model.response.ErrorHandler
import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.domain.repository.TaskRepository
import com.timgortworst.tasqs.presentation.usecase.task.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.threeten.bp.ZonedDateTime

class CompleteTaskUseCaseImpl(
    private val calcNextTaskDate: CalculateNextTaskUseCase,
    private val errorHandler: ErrorHandler,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val createOrUpdateTaskUseCase: CreateOrUpdateTaskUseCase
) : CompleteTaskUseCase {

    data class Params(val tasks: List<Task>)

    override fun execute(params: Params) = flow {
        try {
            val (singleTasks, repeatingTasks) = params.tasks.partition {
                it.metaData.recurrence is TaskRecurrence.SingleTask
            }

            if (singleTasks.isNotEmpty()) {
                // delete single tasks
                deleteTaskUseCase.execute(DeleteTaskUseCaseImpl.Params(singleTasks)).collect()

            } else {
                // update repeating tasks
                repeatingTasks.forEach { task ->

                    val baseDate = if (task.metaData.startDateTime.isBefore(ZonedDateTime.now())) {
                        // if in past, calc the next occurrence from today
                        ZonedDateTime.now()
                    } else {
                        // if in future, calc the next occurrence from previous occurrence
                        task.metaData.startDateTime
                    }

                    task.metaData.startDateTime = calcNextTaskDate.execute(
                        CalculateNextTaskUseCaseImpl.Params(
                            baseDate,
                            task.metaData.recurrence
                        )
                    )

                    createOrUpdateTaskUseCase.execute(
                        CreateOrUpdateTaskUseCaseImpl.Params(task)
                    ).collect()
                }
            }
            emit(Response.Success<Nothing>())
        } catch (e: FirebaseFirestoreException) {
            emit(Response.Error(errorHandler.getError(e)))
        }
    }.flowOn(Dispatchers.IO)
}

