package com.timgortworst.roomy.presentation.usecase

import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.task.CompleteTaskUseCaseImpl

interface CompleteTaskUseCase : UseCase<Nothing, CompleteTaskUseCaseImpl.Params>