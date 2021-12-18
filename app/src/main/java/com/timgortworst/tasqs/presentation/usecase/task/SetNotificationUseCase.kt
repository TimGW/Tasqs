package com.timgortworst.tasqs.presentation.usecase.task

import com.timgortworst.tasqs.domain.usecase.FlowUseCase
import com.timgortworst.tasqs.domain.usecase.task.SetNotificationUseCaseImpl

interface SetNotificationUseCase : FlowUseCase<Nothing, SetNotificationUseCaseImpl.Params>