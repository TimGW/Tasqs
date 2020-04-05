package com.timgortworst.roomy.domain

abstract class UseCase<T> {
    abstract fun executeUseCase() : T
}