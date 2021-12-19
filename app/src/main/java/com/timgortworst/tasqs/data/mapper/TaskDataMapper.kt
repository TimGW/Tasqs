package com.timgortworst.tasqs.data.mapper

import com.timgortworst.tasqs.domain.model.Task
import com.timgortworst.tasqs.domain.model.TaskRecurrence
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId

class TaskDataMapper : Mapper<Map<String, Any>, Task> {

    override fun mapOutgoing(domain: Task): Map<String, Any> {
        val result = mutableMapOf<String, Any>()

        domain.id?.let { result[TASK_ID_REF] = it }
        if (domain.description.isNotBlank()) result[TASK_DESCRIPTION_REF] = domain.description
        result[TASK_META_DATA_REF] = mapOutgoingMetaData(domain.metaData)
        domain.user?.let { result[TASK_USER_REF] = mapOutgoingUser(it) }

        return result
    }

    override fun mapIncoming(network: Map<String, Any>): Task {
        val taskMetaData = network[TASK_META_DATA_REF] as? Map<String, Any?>
        val taskUser = network[TASK_USER_REF] as? Map<String, Any?>

        return Task(
            network[TASK_ID_REF] as? String,
            network[TASK_DESCRIPTION_REF] as? String ?: "",
            taskMetaData?.let { mapIncomingMetaData(it) } ?: Task.MetaData(),
            taskUser?.let { mapIncomingUser(it) }
        )
    }

    private fun mapOutgoingUser(user: Task.User): Map<String, Any?> {
        val result = mutableMapOf<String, Any?>()
        result[USER_ID_REF] = user.userId
        result[USER_NAME_REF] = user.name
        return result
    }

    private fun mapOutgoingMetaData(metaData: Task.MetaData): Map<String, Any?> {
        val result = mutableMapOf<String, Any?>()
        result[TASK_DATE_TIME_REF] = metaData.startDateTime.toInstant().toEpochMilli()
        result[TASK_TIME_ZONE_REF] = metaData.startDateTime.zone.id
        if (metaData.recurrence !is TaskRecurrence.SingleTask) result[TASK_FREQUENCY] = metaData.recurrence.frequency
        result[TASK_RECURRENCE] = metaData.recurrence.id
        (metaData.recurrence as? TaskRecurrence.Weekly)?.let { result[TASK_ON_DAYS] = it.onDaysOfWeek }
        result[TASK_ROTATE_USER] = metaData.rotateUser
        return result
    }

    private fun mapIncomingMetaData(networkMetaData: Map<String, Any?>) : Task.MetaData {
        val startDateTime = networkMetaData[TASK_DATE_TIME_REF] as? Long
        val timeZone = networkMetaData[TASK_TIME_ZONE_REF] as? String
        val metaZonedDateTime = Instant
                    .ofEpochMilli(startDateTime ?: Instant.now().toEpochMilli())
                    .atZone(ZoneId.of(timeZone) ?: ZoneId.systemDefault())

        val recurrenceType =  networkMetaData[TASK_RECURRENCE] as? String
        val recurrenceFrequency =  networkMetaData[TASK_FREQUENCY] as? Long
        val recurrenceWeekDays =  networkMetaData[TASK_ON_DAYS] as? List<Long>
        val metaRecurrence = mapIncomingRecurrence(recurrenceType, recurrenceFrequency?.toInt(), recurrenceWeekDays?.map { it.toInt() })
        val rotateUser = (networkMetaData[TASK_ROTATE_USER] as? Boolean) ?: false

        return Task.MetaData(metaZonedDateTime, metaRecurrence, rotateUser)
    }

    private fun mapIncomingUser(taskUser: Map<String, Any?>): Task.User? {
        val userId = taskUser[USER_ID_REF] as? String ?: return null
        val userName = taskUser[USER_NAME_REF] as? String ?: return null

        return Task.User(userId, userName)
    }

    private fun mapIncomingRecurrence(
        recurrenceType: String?,
        frequency: Int?,
        onDaysOfWeek: List<Int>?
    ): TaskRecurrence {
        val freq = frequency ?: 1

        return when (recurrenceType) {
            TaskRecurrence.DAILY_TASK -> TaskRecurrence.Daily(freq)
            TaskRecurrence.WEEKLY_TASK -> TaskRecurrence.Weekly(freq, onDaysOfWeek ?: emptyList())
            TaskRecurrence.MONTHLY_TASK -> TaskRecurrence.Monthly(freq)
            TaskRecurrence.ANNUAL_TASK -> TaskRecurrence.Annually(freq)
            else -> TaskRecurrence.SingleTask(freq)
        }
    }

    companion object {
        // task
        const val TASK_ID_REF = "id"
        const val TASK_COLLECTION_REF = "tasks"
        const val TASK_USER_REF = "user"
        const val TASK_DESCRIPTION_REF = "description"
        const val TASK_META_DATA_REF = "meta_data"

        // meta-data
        const val TASK_DATE_TIME_REF = "start_datetime"
        const val TASK_TIME_ZONE_REF = "time_zone"
        const val TASK_RECURRENCE = "recurrence_type"
        const val TASK_FREQUENCY = "recurrence_frequency"
        const val TASK_ON_DAYS = "recurrence_weekdays"
        const val TASK_ROTATE_USER = "rotate_user"

        // user
        const val USER_ID_REF = "id"
        const val USER_NAME_REF = "name"
    }
}