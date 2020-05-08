package com.timgortworst.roomy.data.di

import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.model.response.ErrorHandlerImpl
import com.timgortworst.roomy.domain.usecase.account.SignInUseCaseImpl
import com.timgortworst.roomy.domain.usecase.account.ValidationUseCaseImpl
import com.timgortworst.roomy.domain.usecase.ads.AdsVisibleUseCaseImpl
import com.timgortworst.roomy.domain.usecase.settings.EasterEggUseCaseImpl
import com.timgortworst.roomy.domain.usecase.household.SwitchHouseholdUseCaseImpl
import com.timgortworst.roomy.domain.usecase.task.*
import com.timgortworst.roomy.domain.usecase.user.*
import com.timgortworst.roomy.presentation.usecase.household.SwitchHouseholdUseCase
import com.timgortworst.roomy.presentation.usecase.settings.*
import com.timgortworst.roomy.presentation.usecase.signin.SignInUseCase
import com.timgortworst.roomy.presentation.usecase.signin.ValidationUseCase
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
}
