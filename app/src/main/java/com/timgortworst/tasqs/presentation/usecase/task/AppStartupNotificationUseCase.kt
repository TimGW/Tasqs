package com.timgortworst.tasqs.presentation.usecase.task

import com.timgortworst.tasqs.domain.usecase.None
import com.timgortworst.tasqs.domain.usecase.FlowUseCase
import com.timgortworst.tasqs.domain.usecase.task.AppStartupNotificationUseCaseImpl

interface AppStartupNotificationUseCase : FlowUseCase<None, AppStartupNotificationUseCaseImpl.Params>

