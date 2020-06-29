package com.timgortworst.tasqs.presentation.usecase.task

import com.timgortworst.tasqs.domain.usecase.UseCase
import com.timgortworst.tasqs.domain.usecase.task.CompleteTaskUseCaseImpl

interface CompleteTaskUseCase : UseCase<Nothing, CompleteTaskUseCaseImpl.Params>