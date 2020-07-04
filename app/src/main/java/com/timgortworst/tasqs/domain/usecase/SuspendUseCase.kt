package com.timgortworst.tasqs.domain.usecase

interface SuspendUseCase<out T, in Params>  {
    suspend fun execute(params: Params) : T
}