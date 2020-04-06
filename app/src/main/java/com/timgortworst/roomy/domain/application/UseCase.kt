package com.timgortworst.roomy.domain.application

interface UseCase<T> {
    fun invoke() : T
}