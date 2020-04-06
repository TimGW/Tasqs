package com.timgortworst.roomy.domain.usecase

interface UseCase<T> {
    fun invoke() : T
}