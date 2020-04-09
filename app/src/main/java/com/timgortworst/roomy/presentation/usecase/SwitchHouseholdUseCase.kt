package com.timgortworst.roomy.presentation.usecase

import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.household.SwitchHouseholdUseCaseImpl
import com.timgortworst.roomy.presentation.base.model.StartUpAction
import kotlinx.coroutines.flow.Flow

interface SwitchHouseholdUseCase :
    UseCase<Flow<Response<StartUpAction>>, SwitchHouseholdUseCaseImpl.Params>