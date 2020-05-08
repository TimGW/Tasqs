package com.timgortworst.roomy.presentation.usecase.splash

import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.splash.SwitchHouseholdUseCaseImpl
import com.timgortworst.roomy.presentation.base.model.StartUpAction

interface SwitchHouseholdUseCase : UseCase<StartUpAction, SwitchHouseholdUseCaseImpl.Params>