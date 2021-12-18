package com.timgortworst.tasqs.presentation.usecase.splash

import com.timgortworst.tasqs.domain.usecase.FlowUseCase
import com.timgortworst.tasqs.domain.usecase.splash.SwitchHouseholdUseCaseImpl
import com.timgortworst.tasqs.presentation.base.model.StartUpAction

interface SwitchHouseholdUseCase : FlowUseCase<StartUpAction, SwitchHouseholdUseCaseImpl.Params>