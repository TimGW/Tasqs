package com.timgortworst.tasqs.presentation.usecase.task

import com.timgortworst.tasqs.domain.usecase.UseCase
import com.timgortworst.tasqs.domain.usecase.task.DeleteTaskUseCaseImpl

interface DeleteTaskUseCase : UseCase<Nothing, DeleteTaskUseCaseImpl.Params>