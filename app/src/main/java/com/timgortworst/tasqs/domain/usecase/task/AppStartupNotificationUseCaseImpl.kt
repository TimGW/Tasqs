package com.timgortworst.tasqs.domain.usecase.task

import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.domain.repository.TaskRepository
import com.timgortworst.tasqs.domain.usecase.None
import com.timgortworst.tasqs.infrastructure.notifications.NotificationQueue
import com.timgortworst.tasqs.presentation.usecase.task.AppStartupNotificationUseCase
import com.timgortworst.tasqs.presentation.usecase.task.DeleteNotificationsUseCase
import com.timgortworst.tasqs.presentation.usecase.task.SetNotificationUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.threeten.bp.ZonedDateTime.*
import java.time.ZonedDateTime

class AppStartupNotificationUseCaseImpl(
    private val taskRepository: TaskRepository,
    private val notificationQueue: NotificationQueue,
    private val setNotificationUseCase: SetNotificationUseCase
) : AppStartupNotificationUseCase {

    data class Params(
        val userId: String = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    )

    override fun execute(params: Params) = flow {
        val tasks = taskRepository.getTasksForUser(params.userId)

        notificationQueue.removeAllPendingNotifications()

        tasks.forEach {
            if (it.metaData.startDateTime.isAfter(now())) {
                setNotificationUseCase.execute(SetNotificationUseCaseImpl.Params(it)).collect()
            }
        }
        emit(Response.Success(None))
    }.flowOn(Dispatchers.IO)
}

