package com.timgortworst.roomy.domain.usecase

import com.timgortworst.roomy.domain.model.response.Response
import kotlinx.coroutines.flow.Flow

interface UseCase<T, Params> {
    fun execute(params: Params? = null) : Flow<Response<T>>
}