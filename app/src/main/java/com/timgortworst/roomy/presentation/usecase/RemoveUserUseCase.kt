package com.timgortworst.roomy.presentation.usecase

import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.user.RemoveUserUseCaseImpl
import kotlinx.coroutines.flow.Flow

interface RemoveUserUseCase : UseCase<Flow<Response<String>>, RemoveUserUseCaseImpl.Params>