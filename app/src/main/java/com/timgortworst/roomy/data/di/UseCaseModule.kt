package com.timgortworst.roomy.data.di


import com.timgortworst.roomy.domain.usecase.ads.AdsVisibleUseCase
import com.timgortworst.roomy.domain.usecase.easteregg.EasterEggUseCase
import com.timgortworst.roomy.domain.usecase.account.ValidationUseCase
import com.timgortworst.roomy.domain.usecase.account.SignInUseCase
import com.timgortworst.roomy.domain.model.response.ErrorHandlerImpl
import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.usecase.household.SwitchHouseholdUseCase
import com.timgortworst.roomy.domain.usecase.task.CreateOrUpdateTaskUseCase
import com.timgortworst.roomy.domain.usecase.task.GetTasksForUserUseCase
import com.timgortworst.roomy.domain.usecase.user.GetAllUsersUseCase
import com.timgortworst.roomy.domain.usecase.user.GetUserUseCase
import com.timgortworst.roomy.domain.usecase.user.RemoveUserUseCase
import com.timgortworst.roomy.domain.usecase.user.UserUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory<ErrorHandler> { ErrorHandlerImpl() }
    factory {
        GetTasksForUserUseCase(
            get(),
            get()
        )
    }
    factory {
        CreateOrUpdateTaskUseCase(
            get(),
            get()
        )
    }
    factory {
        SignInUseCase(
            get(),
            get(),
            get()
        )
    }
    factory {
        UserUseCase(
            get(),
            get(),
            get(),
            get()
        )
    }
    factory {
        AdsVisibleUseCase(
            get(),
            get()
        )
    }
    factory {
        GetUserUseCase(
            get(),
            get()
        )
    }
    factory {
        EasterEggUseCase(
            get()
        )
    }
    factory {
        SwitchHouseholdUseCase(
            get(),
            get(),
            get(),
            get()
        )
    }
    factory {
        ValidationUseCase(
            get(),
            get()
        )
    }
    factory {
        GetAllUsersUseCase(
            get(),
            get()
        )
    }
    factory {
        RemoveUserUseCase(
            get(),
            get(),
            get(),
            get()
        )
    }
}
