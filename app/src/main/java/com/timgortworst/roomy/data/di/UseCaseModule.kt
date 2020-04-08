package com.timgortworst.roomy.data.di

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.Query
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.model.response.ErrorHandlerImpl
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.usecase.SuspendUseCase
import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.account.SignInUseCase
import com.timgortworst.roomy.domain.usecase.account.ValidationUseCase
import com.timgortworst.roomy.domain.usecase.ads.AdsVisibleUseCase
import com.timgortworst.roomy.domain.usecase.easteregg.EasterEggUseCase
import com.timgortworst.roomy.domain.usecase.household.SwitchHouseholdUseCase
import com.timgortworst.roomy.domain.usecase.task.*
import com.timgortworst.roomy.domain.usecase.user.*
import com.timgortworst.roomy.presentation.base.model.EasterEgg
import com.timgortworst.roomy.presentation.base.model.StartUpAction
import kotlinx.coroutines.flow.Flow
import org.koin.core.qualifier.named
import org.koin.dsl.module

val useCaseModule = module {
    factory<ErrorHandler> { ErrorHandlerImpl() }

    factory<SuspendUseCase<Unit, AddTokenUseCase.Params>>(named("AddTokenUseCase")) {
        AddTokenUseCase(get())
    }
    factory<SuspendUseCase<Query, Unit>>(named("GetTasksForUserUseCase")) {
        GetTasksForUserUseCase(get(), get())
    }
    factory<SuspendUseCase<Query, Unit>>(named("GetAllTasksUseCase")) {
        GetAllTasksUseCase(get())
    }

    factory<UseCase<FirebaseUser?, Unit>>(named("GetFbUserUseCase")) {
        GetFbUserUseCase(get())
    }
    factory<UseCase<Flow<Response<Nothing>>, DeleteTaskUseCase.Params>>(named("DeleteTaskUseCase")) {
        DeleteTaskUseCase(get(), get())
    }
    factory<UseCase<Flow<Response<Nothing>>, CompleteTaskUseCase.Params>>(named("CompleteTaskUseCase")) {
        CompleteTaskUseCase(get(), get())
    }
    factory<UseCase<Flow<Response<Task>>, CreateOrUpdateTaskUseCase.Params>>(named("CreateOrUpdateTaskUseCase")) {
        CreateOrUpdateTaskUseCase(get(), get())
    }
    factory<UseCase<Flow<Response<String>>, SignInUseCase.Params>>(named("SignInUseCase")) {
        SignInUseCase(get(), get(), get(), get(), get())
    }
    factory<UseCase<Boolean, Unit>>(named("AdsVisibleUseCase")) {
        AdsVisibleUseCase(get(), get())
    }
    factory<UseCase<Flow<Response<User>>, GetUserUseCase.Params>>(named("GetUserUseCase")) {
        GetUserUseCase(get(), get())
    }
    factory<UseCase<EasterEgg?, EasterEggUseCase.Params>>(named("EasterEggUseCase")) {
        EasterEggUseCase(get())
    }
    factory<UseCase<Flow<Response<StartUpAction>>, SwitchHouseholdUseCase.Params>>(named("SwitchHouseholdUseCase")) {
        SwitchHouseholdUseCase(get(), get(), get(), get(), get())
    }
    factory<UseCase<Flow<Response<StartUpAction>>, ValidationUseCase.Params>>(named("ValidationUseCase")) {
        ValidationUseCase(get(), get(), get())
    }
    factory<UseCase<LiveData<Response<List<User>>>, Unit>>(named("GetAllUsersUseCase")) {
        GetAllUsersUseCase(get(), get())
    }
    factory<UseCase<Flow<Response<String>>, RemoveUserUseCase.Params>>(named("RemoveUserUseCase")) {
        RemoveUserUseCase(get(), get(), get(), get())
    }
}
