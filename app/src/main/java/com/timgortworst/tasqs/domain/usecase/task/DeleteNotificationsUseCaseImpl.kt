package com.timgortworst.tasqs.domain.usecase.task

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.tasqs.domain.model.Task
import com.timgortworst.tasqs.domain.model.response.ErrorHandler
import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.infrastructure.notifications.NotificationQueue
import com.timgortworst.tasqs.presentation.usecase.task.DeleteNotificationsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class DeleteNotificationsUseCaseImpl(
    private val errorHandler: ErrorHandler,
    private val notificationQueue: NotificationQueue
) : DeleteNotificationsUseCase {

    data class Params(val tasks: List<Task>)

    override fun execute(params: Params) = flow {
        emit(Response.Loading)

        try {
            params.tasks.forEach {
                val taskId = it.id ?: return@forEach
                notificationQueue.removePendingNotification(taskId)

                Log.i(TAG, "Notification removed for: $taskId")
            }

            emit(Response.Success())
        } catch (e: FirebaseFirestoreException) {
            emit(Response.Error(errorHandler.getError(e)))
        }
    }.flowOn(Dispatchers.Default)

    companion object {
        private const val TAG = "DeleteNotificationUseCase"
    }
}

