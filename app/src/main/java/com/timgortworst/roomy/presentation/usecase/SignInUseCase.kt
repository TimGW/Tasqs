package com.timgortworst.roomy.presentation.usecase

import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.account.SignInUseCaseImpl
import kotlinx.coroutines.flow.Flow

interface SignInUseCase : UseCase<Flow<Response<String>>, SignInUseCaseImpl.Params>