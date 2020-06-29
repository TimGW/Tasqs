package com.timgortworst.tasqs.presentation.usecase.splash

import com.timgortworst.tasqs.domain.usecase.UseCase
import com.timgortworst.tasqs.domain.usecase.splash.SwitchHouseholdUseCaseImpl
import com.timgortworst.tasqs.presentation.base.model.StartUpAction

interface SwitchHouseholdUseCase : UseCase<StartUpAction, SwitchHouseholdUseCaseImpl.Params>