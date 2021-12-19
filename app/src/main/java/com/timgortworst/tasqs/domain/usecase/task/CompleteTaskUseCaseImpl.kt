package com.timgortworst.tasqs.domain.usecase.task

import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.tasqs.domain.model.Task
import com.timgortworst.tasqs.domain.model.TaskRecurrence
import com.timgortworst.tasqs.domain.model.response.ErrorHandler
import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.domain.usecase.None
import com.timgortworst.tasqs.infrastructure.extension.getOrFirst
import com.timgortworst.tasqs.presentation.usecase.task.*
import com.timgortworst.tasqs.presentation.usecase.user.GetAllUsersUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.threeten.bp.ZonedDateTime

class CompleteTaskUseCaseImpl(
    private val calcNextTaskDate: CalculateNextTaskUseCase,
    private val errorHandler: ErrorHandler,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val createOrUpdateTaskUseCase: CreateOrUpdateTaskUseCase,
    private val getAllUsersUseCase: GetAllUsersUseCase
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

                    if (task.metaData.rotateUser) {
                        getAllUsersUseCase.execute(None).collect { response ->
                            val users = (response as? Response.Success)?.data

                            val indexOfAssignedUser = users?.indexOfFirst {
                                it.userId == task.user?.userId
                            } ?: 0

                            users?.getOrFirst(indexOfAssignedUser + 1)?.let {
                                task.user = Task.User(it.userId, it.name)
                            }
                        }
                    }

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

