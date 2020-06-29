package com.timgortworst.tasqs.domain.usecase

import com.timgortworst.tasqs.domain.model.response.Response
import kotlinx.coroutines.flow.Flow

interface UseCase<T, Params> {
    fun execute(params: Params? = null) : Flow<Response<T>>
}