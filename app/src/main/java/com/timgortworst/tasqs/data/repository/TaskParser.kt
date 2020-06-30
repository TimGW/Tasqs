package com.timgortworst.tasqs.data.repository

import com.timgortworst.tasqs.domain.model.Task
import com.timgortworst.tasqs.domain.model.TaskMetaData
import com.timgortworst.tasqs.domain.model.TaskRecurrence
import com.timgortworst.tasqs.domain.model.TaskRecurrence.Companion.ANNUAL_TASK
import com.timgortworst.tasqs.domain.model.TaskRecurrence.Companion.DAILY_TASK
import com.timgortworst.tasqs.domain.model.TaskRecurrence.Companion.MONTHLY_TASK
import com.timgortworst.tasqs.domain.model.TaskRecurrence.Companion.WEEKLY_TASK
import com.timgortworst.tasqs.domain.model.firestore.TaskJson
import com.timgortworst.tasqs.domain.model.firestore.TaskMetaDataJson
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId

/**
 * Custom object mapper class to convert firestore data models to app models and vice-versa
 */
object TaskParser {
    fun toTask(taskJson: TaskJson): Task? {
        if (
            taskJson.id == null ||
            taskJson.description == null ||
            taskJson.metaData == null ||
            taskJson.user == null
        ) {
            return null
        }

        return Task(
            taskJson.id!!,
            taskJson.description!!,
            taskJson.metaData!!.toTaskMetaData(),
            taskJson.user!!
        )
    }

    private fun TaskMetaDataJson.toTaskMetaData(): TaskMetaData {
        return TaskMetaData(
            Instant
                .ofEpochMilli(startDateTime!!)
                .atZone(ZoneId.of(timeZone!!)),
            buildRecurrence(
                recurrenceType,
                frequency ?: 1,
                onDaysOfWeek.orEmpty()
            )
        )
    }

    private fun buildRecurrence(
        recurrenceType: String?,
        frequency: Int,
        onDaysOfWeek: List<Int>
    ): TaskRecurrence {
        return when (recurrenceType) {
            DAILY_TASK -> TaskRecurrence.Daily(frequency)
            WEEKLY_TASK -> TaskRecurrence.Weekly(frequency, onDaysOfWeek)
            MONTHLY_TASK -> TaskRecurrence.Monthly(frequency)
            ANNUAL_TASK -> TaskRecurrence.Annually(frequency)
            else -> TaskRecurrence.SingleTask(frequency)
        }
    }

    fun convertToMap(task: Task): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        result[TaskJson.TASK_ID_REF] = task.id
        result[TaskJson.TASK_DESCRIPTION_REF] = task.description
        result[TaskJson.TASK_META_DATA_REF] = task.metaData.toMap()
        result[TaskJson.TASK_USER_REF] = task.user
        return result
    }

    private fun TaskMetaData.toMap(): Map<String, Any?> {
        val result = mutableMapOf<String, Any?>()
        result[TaskMetaDataJson.TASK_DATE_TIME_REF] = startDateTime.toInstant().toEpochMilli()
        result[TaskMetaDataJson.TASK_TIME_ZONE_REF] = startDateTime.zone.id
        if (recurrence !is TaskRecurrence.SingleTask) result[TaskMetaDataJson.TASK_FREQUENCY] =
            recurrence.frequency
        result[TaskMetaDataJson.TASK_RECURRENCE] = recurrence.id
        (recurrence as? TaskRecurrence.Weekly)?.let {
            result[TaskMetaDataJson.TASK_ON_DAYS] = it.onDaysOfWeek
        }
        return result
    }
}