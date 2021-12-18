package com.timgortworst.tasqs.presentation.usecase.task

import com.timgortworst.tasqs.domain.usecase.FlowUseCase
import com.timgortworst.tasqs.domain.usecase.task.DeleteTaskUseCaseImpl

interface DeleteTaskUseCase : FlowUseCase<Nothing, DeleteTaskUseCaseImpl.Params>