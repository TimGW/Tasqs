package com.timgortworst.roomy.presentation.usecase.signin

import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.account.ValidationUseCaseImpl
import com.timgortworst.roomy.presentation.base.model.StartUpAction

interface ValidationUseCase : UseCase<StartUpAction, ValidationUseCaseImpl.Params>