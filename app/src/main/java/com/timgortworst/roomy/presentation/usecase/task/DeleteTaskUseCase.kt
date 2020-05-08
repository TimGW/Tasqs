package com.timgortworst.roomy.presentation.usecase.task

import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.task.DeleteTaskUseCaseImpl

interface DeleteTaskUseCase : UseCase<Nothing, DeleteTaskUseCaseImpl.Params>