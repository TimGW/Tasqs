package com.timgortworst.roomy.presentation.usecase

import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.account.ValidationUseCaseImpl
import com.timgortworst.roomy.presentation.base.model.StartUpAction
import kotlinx.coroutines.flow.Flow

interface ValidationUseCase : UseCase<Flow<Response<StartUpAction>>, ValidationUseCaseImpl.Params>