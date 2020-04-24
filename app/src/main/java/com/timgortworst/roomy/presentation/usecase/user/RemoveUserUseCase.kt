package com.timgortworst.roomy.presentation.usecase.user

import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.user.RemoveUserUseCaseImpl
import kotlinx.coroutines.flow.Flow

interface RemoveUserUseCase : UseCase<String, RemoveUserUseCaseImpl.Params>