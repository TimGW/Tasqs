package com.timgortworst.tasqs.presentation.usecase.task

import com.timgortworst.tasqs.domain.usecase.FlowUseCase
import com.timgortworst.tasqs.domain.usecase.task.CompleteTaskUseCaseImpl

interface CompleteTaskUseCase : FlowUseCase<Nothing, CompleteTaskUseCaseImpl.Params>