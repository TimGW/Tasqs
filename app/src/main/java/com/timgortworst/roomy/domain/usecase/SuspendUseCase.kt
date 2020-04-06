package com.timgortworst.roomy.domain.usecase

interface SuspendUseCase<T> {
    suspend fun invoke() : T
}