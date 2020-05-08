package com.timgortworst.roomy.presentation.usecase.task

import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.task.CalculateNextTaskUseCaseImpl
import org.threeten.bp.ZonedDateTime

interface CalculateNextTaskUseCase : UseCase<ZonedDateTime, CalculateNextTaskUseCaseImpl.Params>