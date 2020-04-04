package com.timgortworst.roomy.data.di


import com.timgortworst.roomy.domain.model.response.ErrorHandlerImpl
import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.usecase.*
import org.koin.dsl.module

val useCaseModule = module {
    factory { TaskListUseCase(get(), get()) }
    factory { TaskEditUseCase(get(), get(), get(), get()) }
    factory { SignInUseCase(get(), get(), get()) }
    factory { UserUseCase(get(), get(), get(), get()) }
    factory { SplashUseCase(get(), get(), get(), get()) }
    factory { SettingsUseCase(get(), get(), get()) }
    factory { MainUseCase(get(), get(), get()) }
    factory<ErrorHandler> { ErrorHandlerImpl() }
}
