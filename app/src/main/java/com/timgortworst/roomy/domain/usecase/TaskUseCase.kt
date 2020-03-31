package com.timgortworst.roomy.domain.usecase

import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.TaskRepository
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.TaskMetaData
import com.timgortworst.roomy.domain.model.TaskRecurrence
import com.timgortworst.roomy.domain.model.TaskUser
import com.timgortworst.roomy.domain.utils.TimeOperations
import org.threeten.bp.LocalTime
import org.threeten.bp.ZonedDateTime

class TaskUseCase(
    private val taskRepository: TaskRepository
) {
    suspend fun getAllTasksQuery() = taskRepository.getAllTasksQuery()

    suspend fun getTasksForUserQuery(
        userId: String = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    ) = taskRepository.getTasksForUserQuery(userId)

    suspend fun deleteTasks(tasks: List<Task>) {
        taskRepository.deleteTasks(tasks)
    }

    suspend fun tasksCompleted(tasks: List<Task>) {
        tasks.filter {
            it.metaData.recurrence is TaskRecurrence.SingleTask
        }.run {
            deleteTasks(this)
        }

        tasks.filterNot {
            it.metaData.recurrence is TaskRecurrence.SingleTask
        }.run {
            updateNextTaskDate(this)
        }
    }

    private suspend fun updateNextTaskDate(tasks: List<Task>) {
        tasks.forEach {
            it.metaData.startDateTime = calcNextTaskDate(it.metaData)
            it.isDoneEnabled = false // temporary disable the done button
        }

        taskRepository.updateTasks(tasks)
    }

    private fun calcNextTaskDate(taskMetaData: TaskMetaData): ZonedDateTime {
        val timeOperations = TimeOperations()
        return if (taskMetaData.startDateTime.isBefore(ZonedDateTime.now())) {
            val noon = ZonedDateTime.now().with(LocalTime.NOON)
            timeOperations.nextTask(noon, taskMetaData.recurrence)
        } else {
            timeOperations.nextTask(taskMetaData.startDateTime, taskMetaData.recurrence)
        }
    }

    suspend fun createOrUpdateTask(task: Task) {
        // temporary disable the done button
        val result = task.apply { isDoneEnabled = false }

        if (task.id.isEmpty()) {
            taskRepository.createTask(result)
        } else {
            taskRepository.updateTask(result)
        }
    }
}

