package com.timgortworst.roomy.presentation.usecase

import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.user.AddTokenUseCaseImpl
import kotlinx.coroutines.flow.Flow

interface AddTokenUseCase : UseCase<Flow<Response<Nothing>>, AddTokenUseCaseImpl.Params>