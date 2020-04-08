package com.timgortworst.roomy.domain.usecase

interface UseCase<T, Params> {
    fun execute(params: Params? = null) : T
}