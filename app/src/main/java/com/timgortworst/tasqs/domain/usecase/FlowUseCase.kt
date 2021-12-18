package com.timgortworst.tasqs.domain.usecase

import com.timgortworst.tasqs.domain.model.response.Response
import kotlinx.coroutines.flow.Flow

interface FlowUseCase<out T, in Params> : UseCase<Flow<Response<T>>, Params> {
    override fun execute(params: Params): Flow<Response<T>>
}