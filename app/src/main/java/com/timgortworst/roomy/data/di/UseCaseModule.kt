package com.timgortworst.roomy.data.di

import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.model.response.ErrorHandlerImpl
import com.timgortworst.roomy.domain.usecase.settings.AdsVisibleUseCaseImpl
import com.timgortworst.roomy.domain.usecase.settings.EasterEggUseCaseImpl
import com.timgortworst.roomy.domain.usecase.signin.SignInUseCaseImpl
import com.timgortworst.roomy.domain.usecase.splash.ForcedUpdateUseCaseImpl
import com.timgortworst.roomy.domain.usecase.splash.SwitchHouseholdUseCaseImpl
import com.timgortworst.roomy.domain.usecase.splash.ValidationUseCaseImpl
import com.timgortworst.roomy.domain.usecase.task.*
import com.timgortworst.roomy.domain.usecase.user.*
import com.timgortworst.roomy.presentation.usecase.settings.AdsVisibleUseCase
import com.timgortworst.roomy.presentation.usecase.settings.EasterEggUseCase
import com.timgortworst.roomy.presentation.usecase.signin.SignInUseCase
import com.timgortworst.roomy.presentation.usecase.splash.ForcedUpdateUseCase
import com.timgortworst.roomy.presentation.usecase.splash.SwitchHouseholdUseCase
import com.timgortworst.roomy.presentation.usecase.splash.ValidationUseCase
import com.timgortworst.roomy.presentation.usecase.task.*
import com.timgortworst.roomy.presentation.usecase.user.*
import org.koin.dsl.module

val useCaseModule = module {
    factory<ErrorHandler> { ErrorHandlerImpl() }

    factory<AddTokenUseCase> { AddTokenUseCaseImpl(get(), get()) }
    factory<GetTasksForUserUseCase> { GetTasksForUserUseCaseImpl(get(), get()) }
    factory<GetAllTasksUseCase> { GetAllTasksUseCaseImpl(get()) }
    factory<GetFbUserUseCase> { GetFbUserUseCaseImpl(get(), get()) }
    factory<DeleteTaskUseCase> { DeleteTaskUseCaseImpl(get(), get()) }
    factory<CompleteTaskUseCase> { CompleteTaskUseCaseImpl(get(), get(), get()) }
    factory<CalculateNextTaskUseCase> { CalculateNextTaskUseCaseImpl() }
    factory<CreateOrUpdateTaskUseCase> { CreateOrUpdateTaskUseCaseImpl(get(), get()) }
    factory<SignInUseCase> { SignInUseCaseImpl(get(), get(), get()) }
    factory<AdsVisibleUseCase> { AdsVisibleUseCaseImpl(get()) }
    factory<GetUserUseCase> { GetUserUseCaseImpl(get(), get()) }
    factory<EasterEggUseCase> { EasterEggUseCaseImpl(get()) }
    factory<SwitchHouseholdUseCase> { SwitchHouseholdUseCaseImpl(get(), get(), get(), get()) }
    factory<ValidationUseCase> { ValidationUseCaseImpl(get(), get()) }
    factory<GetAllUsersUseCase> { GetAllUsersUseCaseImpl(get(), get()) }
    factory<RemoveUserUseCase> { RemoveUserUseCaseImpl(get(), get(), get(), get()) }
    factory<InviteLinkBuilderUseCase> { InviteLinkBuilderUseCaseImpl(get()) }
    factory<ForcedUpdateUseCase> { ForcedUpdateUseCaseImpl() }
    factory<GetTaskUseCase> { GetTaskUseCaseImpl(get(), get()) }
}
