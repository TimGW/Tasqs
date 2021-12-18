package com.timgortworst.tasqs.domain.usecase

interface UseCase<out T, in Params> {
    fun execute(params: Params) : T
}