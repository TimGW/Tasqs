package com.timgortworst.tasqs.presentation.usecase.task

import com.timgortworst.tasqs.domain.usecase.UseCase
import com.timgortworst.tasqs.domain.usecase.task.CalculateNextTaskUseCaseImpl
import org.threeten.bp.ZonedDateTime

interface CalculateNextTaskUseCase : UseCase<ZonedDateTime, CalculateNextTaskUseCaseImpl.Params>