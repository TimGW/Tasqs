package com.timgortworst.tasqs.presentation.usecase.user

import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.domain.usecase.UseCase
import com.timgortworst.tasqs.domain.usecase.user.AddTokenUseCaseImpl
import kotlinx.coroutines.flow.Flow

interface AddTokenUseCase : UseCase<Flow<Response<Nothing>>, AddTokenUseCaseImpl.Params>