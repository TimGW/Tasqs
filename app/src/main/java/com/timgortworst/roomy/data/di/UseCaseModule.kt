package com.timgortworst.roomy.data.di


import com.timgortworst.roomy.domain.usecase.EventUseCase
import com.timgortworst.roomy.domain.usecase.MainUseCase
import com.timgortworst.roomy.domain.usecase.SetupUseCase
import com.timgortworst.roomy.domain.usecase.UserUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { EventUseCase(get(), get()) }
    factory { MainUseCase(get(), get()) }
    factory { SetupUseCase(get(), get(), get()) }
    factory { UserUseCase(get(), get(), get()) }
}
