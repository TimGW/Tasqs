package com.timgortworst.tasqs.presentation.usecase.splash

import com.timgortworst.tasqs.domain.usecase.UseCase
import com.timgortworst.tasqs.domain.usecase.splash.ValidationUseCaseImpl
import com.timgortworst.tasqs.presentation.base.model.StartUpAction

interface ValidationUseCase : UseCase<StartUpAction, ValidationUseCaseImpl.Params>