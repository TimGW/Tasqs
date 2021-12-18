package com.timgortworst.tasqs.presentation.usecase.splash

import com.timgortworst.tasqs.domain.usecase.FlowUseCase
import com.timgortworst.tasqs.domain.usecase.splash.ValidationUseCaseImpl
import com.timgortworst.tasqs.presentation.base.model.StartUpAction

interface ValidationUseCase : FlowUseCase<StartUpAction, ValidationUseCaseImpl.Params>