package com.timgortworst.tasqs.data.di

import com.timgortworst.tasqs.domain.model.response.ErrorHandler
import com.timgortworst.tasqs.domain.model.response.ErrorHandlerImpl
import com.timgortworst.tasqs.domain.usecase.settings.AdsVisibleUseCaseImpl
import com.timgortworst.tasqs.domain.usecase.settings.EasterEggUseCaseImpl
import com.timgortworst.tasqs.domain.usecase.signin.SignInUseCaseImpl
import com.timgortworst.tasqs.domain.usecase.splash.ForcedUpdateUseCaseImpl
import com.timgortworst.tasqs.domain.usecase.splash.SwitchHouseholdUseCaseImpl
import com.timgortworst.tasqs.domain.usecase.splash.ValidationUseCaseImpl
import com.timgortworst.tasqs.domain.usecase.task.*
import com.timgortworst.tasqs.domain.usecase.user.*
import com.timgortworst.tasqs.presentation.usecase.settings.AdsVisibleUseCase
import com.timgortworst.tasqs.presentation.usecase.settings.EasterEggUseCase
import com.timgortworst.tasqs.presentation.usecase.signin.SignInUseCase
import com.timgortworst.tasqs.presentation.usecase.splash.ForcedUpdateUseCase
import com.timgortworst.tasqs.presentation.usecase.splash.SwitchHouseholdUseCase
import com.timgortworst.tasqs.presentation.usecase.splash.ValidationUseCase
import com.timgortworst.tasqs.presentation.usecase.task.*
import com.timgortworst.tasqs.presentation.usecase.user.*
import org.koin.dsl.module

val useCaseModule = module {
    factory<ErrorHandler> { ErrorHandlerImpl() }

    factory<GetTasksForUserQueryUseCase> { GetTasksForUserQueryUseCaseImpl(get()) }
    factory<GetAllTasksUseCase> { GetAllTasksUseCaseImpl(get()) }
    factory<DeleteTaskUseCase> { DeleteTaskUseCaseImpl(get(), get(), get()) }
    factory<CompleteTaskUseCase> { CompleteTaskUseCaseImpl(get(), get(), get(), get(), get()) }
    factory<CalculateNextTaskUseCase> { CalculateNextTaskUseCaseImpl() }
    factory<CreateOrUpdateTaskUseCase> { CreateOrUpdateTaskUseCaseImpl(get(), get(), get(), get()) }
    factory<SignInUseCase> { SignInUseCaseImpl(get(), get(), get()) }
    factory<AdsVisibleUseCase> { AdsVisibleUseCaseImpl() }
    factory<GetUserUseCase> { GetUserUseCaseImpl(get(), get()) }
    factory<EasterEggUseCase> { EasterEggUseCaseImpl() }
    factory<SwitchHouseholdUseCase> { SwitchHouseholdUseCaseImpl(get(), get(), get(), get()) }
    factory<ValidationUseCase> { ValidationUseCaseImpl(get(), get()) }
    factory<GetAllUsersUseCase> { GetAllUsersUseCaseImpl(get(), get()) }
    factory<RemoveUserUseCase> { RemoveUserUseCaseImpl(get(), get(), get(), get()) }
    factory<InviteLinkBuilderUseCase> { InviteLinkBuilderUseCaseImpl(get()) }
    factory<ForcedUpdateUseCase> { ForcedUpdateUseCaseImpl() }
    factory<GetTaskUseCase> { GetTaskUseCaseImpl(get(), get()) }
    factory<DeleteNotificationsUseCase> { DeleteNotificationsUseCaseImpl(get(), get()) }
    factory<SetNotificationUseCase> { SetNotificationUseCaseImpl(get(), get(), get()) }
    factory<AppStartupNotificationUseCase> { AppStartupNotificationUseCaseImpl(get(), get(), get()) }
}
