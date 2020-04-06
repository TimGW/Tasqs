package com.timgortworst.roomy.domain.usecase.task

import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.TaskMetaData
import com.timgortworst.roomy.domain.model.TaskRecurrence
import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.repository.TaskRepository
import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.utils.TimeOperations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.threeten.bp.LocalTime
import org.threeten.bp.ZonedDateTime

class CompleteTaskUseCase(
    private val taskRepository: TaskRepository,
    private val errorHandler: ErrorHandler
) : UseCase<Flow<Response<Nothing>>> {
    private lateinit var tasks: List<Task>

    fun init(tasks: List<Task>): CompleteTaskUseCase {
        this.tasks = tasks
        return this
    }
    override fun invoke()= flow {
        emit(Response.Loading)

        try {
            tasks.filter {
                it.metaData.recurrence is TaskRecurrence.SingleTask
            }.run {
                taskRepository.deleteTasks(this)
            }

            tasks.filterNot {
                it.metaData.recurrence is TaskRecurrence.SingleTask
            }.run {
                forEach {
                    it.metaData.startDateTime = calcNextTaskDate(it.metaData)
                    it.isDoneEnabled = false // temporary disable the done button
                }

                taskRepository.updateTasks(this)
            }
            emit(Response.Success())
        } catch (e: FirebaseFirestoreException) {
            emit(Response.Error(errorHandler.getError(e)))
        }
    }.flowOn(Dispatchers.IO)

    private fun calcNextTaskDate(taskMetaData: TaskMetaData): ZonedDateTime {
        val timeOperations = TimeOperations()
        return if (taskMetaData.startDateTime.isBefore(ZonedDateTime.now())) {
            val noon = ZonedDateTime.now().with(LocalTime.NOON)
            timeOperations.nextTask(noon, taskMetaData.recurrence)
        } else {
            timeOperations.nextTask(taskMetaData.startDateTime, taskMetaData.recurrence)
        }
    }
}

