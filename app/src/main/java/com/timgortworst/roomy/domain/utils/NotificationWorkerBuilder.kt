package com.timgortworst.roomy.domain.utils

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.timgortworst.roomy.R
import com.timgortworst.roomy.domain.model.TaskMetaData
import com.timgortworst.roomy.domain.model.TaskRecurrence
import org.threeten.bp.Duration
import org.threeten.bp.ZonedDateTime
import java.util.concurrent.TimeUnit
import kotlin.math.max

class NotificationWorkerBuilder(private val context: Context) {
    private val workManager = WorkManager.getInstance(context)

    fun enqueueNotification(taskId: String,
                            taskMetaData: TaskMetaData,
                            userName: String,
                            categoryName: String) {
        removePendingNotificationReminder(taskId)

        val delay = max(0L, Duration.between(ZonedDateTime.now(), taskMetaData.startDateTime).toMillis())
        val inputData = buildInputData(taskId, userName, categoryName, taskMetaData.recurrence)
        val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()

        val workRequest = OneTimeWorkRequest.Builder(ReminderNotificationWorker::class.java)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag(taskId)
                .setInputData(inputData)
                .setConstraints(constraints)
                .build()

        workManager.enqueue(workRequest)
    }

    private fun buildInputData(taskId: String,
                               userName: String,
                               categoryName: String,
                               taskRecurrence: TaskRecurrence): Data {

        val title = context.getString(R.string.notification_title, userName)
        val msg = context.getString(R.string.notification_message, categoryName)
        val dataBuilder = Data.Builder()
                .putString(NOTIFICATION_ID_KEY, taskId)
                .putString(NOTIFICATION_TITLE_KEY, title)
                .putString(NOTIFICATION_MSG_KEY, msg)

        return dataBuilder.build()
    }

    fun removePendingNotificationReminder(taskId: String) {
        workManager.cancelAllWorkByTag(taskId)
    }

    companion object {
        const val NOTIFICATION_ID_KEY = "NOTIFICATION_ID_KEY"
        const val NOTIFICATION_TITLE_KEY = "NOTIFICATION_TITLE_KEY"
        const val NOTIFICATION_MSG_KEY = "NOTIFICATION_MSG_KEY"
    }
}