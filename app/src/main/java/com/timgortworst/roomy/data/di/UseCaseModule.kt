package com.timgortworst.roomy.data.di


import com.timgortworst.roomy.domain.usecase.HouseholdUseCase
import com.timgortworst.roomy.domain.usecase.LoginUseCase
import com.timgortworst.roomy.domain.usecase.TaskUseCase
import com.timgortworst.roomy.domain.usecase.UserUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { TaskUseCase(get()) }
    factory { LoginUseCase(get(), get()) }
    factory { UserUseCase(get(), get(), get()) }
    factory { HouseholdUseCase(get(), get(), get()) }
}
