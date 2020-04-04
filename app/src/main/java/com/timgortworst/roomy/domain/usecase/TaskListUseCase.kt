package com.timgortworst.roomy.domain.usecase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.data.repository.TaskRepository
import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.model.task.Task
import com.timgortworst.roomy.domain.model.task.TaskMetaData
import com.timgortworst.roomy.domain.model.task.TaskRecurrence
import com.timgortworst.roomy.domain.utils.TimeOperations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import org.threeten.bp.LocalTime
import org.threeten.bp.ZonedDateTime

class TaskListUseCase(
    private val taskRepository: TaskRepository,
    private val errorHandler: ErrorHandler
) {
    suspend fun getAllTasksQuery() = taskRepository.getAllTasksQuery()

    suspend fun getTasksForUserQuery(
        userId: String = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    ) = taskRepository.getTasksForUserQuery(userId)

    fun deleteTasks(tasks: List<Task>) = flow {
        emit(Response.Loading)
        try {
            taskRepository.deleteTasks(tasks)
            emit(Response.Success())
        } catch (e: FirebaseFirestoreException) {
            emit(Response.Error(errorHandler.getError(e)))
        }
    }.flowOn(Dispatchers.IO)


    fun tasksCompleted(tasks: List<Task>) = flow {
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

