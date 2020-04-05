package com.timgortworst.roomy.domain

interface UseCase<T> {
    fun executeUseCase() : T
//    fun<P, T> executeUseCase(vararg prop: P?, funCreateSpec: P) : T
}