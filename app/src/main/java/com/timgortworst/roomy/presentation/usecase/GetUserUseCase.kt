package com.timgortworst.roomy.presentation.usecase

import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.user.GetUserUseCaseImpl
import kotlinx.coroutines.flow.Flow

interface GetUserUseCase : UseCase<Flow<Response<User>>, GetUserUseCaseImpl.Params>