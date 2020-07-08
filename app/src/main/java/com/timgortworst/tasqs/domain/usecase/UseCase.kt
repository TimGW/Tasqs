package com.timgortworst.tasqs.domain.usecase

import com.timgortworst.tasqs.domain.model.response.Response
import kotlinx.coroutines.flow.Flow

interface UseCase<out T, in Params> {
    fun execute(params: Params) : Flow<Response<T>>
}