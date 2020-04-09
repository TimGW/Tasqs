package com.timgortworst.roomy.presentation.usecase

import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.household.SwitchHouseholdUseCaseImpl
import com.timgortworst.roomy.presentation.base.model.StartUpAction

interface SwitchHouseholdUseCase : UseCase<StartUpAction, SwitchHouseholdUseCaseImpl.Params>