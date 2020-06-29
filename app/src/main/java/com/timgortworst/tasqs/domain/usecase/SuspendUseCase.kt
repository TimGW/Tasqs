package com.timgortworst.tasqs.domain.usecase

interface SuspendUseCase<T, Params> {
    suspend fun execute(params: Params? = null) : T
}