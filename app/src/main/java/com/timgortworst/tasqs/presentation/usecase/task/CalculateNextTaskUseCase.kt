package com.timgortworst.tasqs.presentation.usecase.task

import com.timgortworst.tasqs.domain.usecase.FlowUseCase
import com.timgortworst.tasqs.domain.usecase.task.CalculateNextTaskUseCaseImpl
import org.threeten.bp.ZonedDateTime

interface CalculateNextTaskUseCase : FlowUseCase<ZonedDateTime, CalculateNextTaskUseCaseImpl.Params>