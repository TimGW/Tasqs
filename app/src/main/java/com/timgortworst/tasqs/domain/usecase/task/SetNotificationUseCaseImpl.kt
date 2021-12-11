package com.timgortworst.tasqs.domain.usecase.task

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.tasqs.BuildConfig
import com.timgortworst.tasqs.domain.model.Task
import com.timgortworst.tasqs.domain.model.response.ErrorHandler
import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.infrastructure.notifications.NotificationQueue
import com.timgortworst.tasqs.infrastructure.notifications.Notifications
import com.timgortworst.tasqs.presentation.usecase.task.SetNotificationUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.threeten.bp.ZonedDateTime

class SetNotificationUseCaseImpl(
    private val errorHandler: ErrorHandler,
    private val notificationQueue: NotificationQueue,
    private val notifications: Notifications
) : SetNotificationUseCase {

    data class Params(val task: Task)

    override fun execute(params: Params) = flow {
        emit(Response.Loading)

        try {
            setPendingNotification(
                params.task.id ?: return@flow,
                params.task.metaData.startDateTime,
                params.task.user?.name.orEmpty(),
                params.task.description
            )
            emit(Response.Success())
        } catch (e: FirebaseFirestoreException) {
            emit(Response.Error(errorHandler.getError(e)))
        }
    }.flowOn(Dispatchers.IO)

    private fun setPendingNotification(
        taskId: String,
        zonedDateTime: ZonedDateTime,
        userName: String,
        taskDescription: String
    ) {
        if (zonedDateTime.isBefore(ZonedDateTime.now())) {
            notifications.notify(taskId, userName, taskDescription)
        } else {
            notificationQueue.enqueueNotification(
                taskId,
                zonedDateTime,
                userName,
                taskDescription
            )

            val debugNotificationText =
                "username: $userName\n" +
                        "date: ${zonedDateTime.dayOfMonth}-${zonedDateTime.monthValue}-${zonedDateTime.year} " +
                        "time: ${zonedDateTime.hour}:${zonedDateTime.minute} " +
                        "zone: ${zonedDateTime.zone}"

            Log.i(TAG, "Notification set for: $debugNotificationText")
        }
    }

    companion object {
        private const val TAG = "SetNotificationUseCase"
    }
}

