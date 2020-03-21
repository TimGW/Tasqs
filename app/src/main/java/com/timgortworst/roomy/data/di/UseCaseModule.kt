package com.timgortworst.roomy.data.di


import com.timgortworst.roomy.domain.usecase.TaskUseCase
import com.timgortworst.roomy.domain.usecase.SetupUseCase
import com.timgortworst.roomy.domain.usecase.UserUseCase
import com.timgortworst.roomy.domain.utils.NotificationWorkManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val useCaseModule = module {
    single { NotificationWorkManager(androidContext()) }
    factory { TaskUseCase(get(), get(), get()) }
    factory { SetupUseCase(get(), get(), get(), get()) }
    factory { UserUseCase(get(), get(), get(), get()) }
}
