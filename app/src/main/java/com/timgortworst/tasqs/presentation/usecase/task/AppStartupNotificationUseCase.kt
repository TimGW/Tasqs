package com.timgortworst.tasqs.presentation.usecase.task

import com.timgortworst.tasqs.domain.usecase.None
import com.timgortworst.tasqs.domain.usecase.UseCase
import com.timgortworst.tasqs.domain.usecase.task.AppStartupNotificationUseCaseImpl

interface AppStartupNotificationUseCase : UseCase<None, AppStartupNotificationUseCaseImpl.Params>

