package com.timgortworst.roomy.data.di


import com.timgortworst.roomy.domain.application.AdsVisibleUseCase
import com.timgortworst.roomy.domain.application.EasterEggUseCase
import com.timgortworst.roomy.domain.application.account.ValidationUseCase
import com.timgortworst.roomy.domain.application.account.SignInUseCase
import com.timgortworst.roomy.domain.model.response.ErrorHandlerImpl
import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.application.household.SwitchHouseholdUseCase
import com.timgortworst.roomy.domain.application.task.CreateOrUpdateTaskUseCase
import com.timgortworst.roomy.domain.application.task.TaskListUseCase
import com.timgortworst.roomy.domain.application.user.GetAllUsersUseCase
import com.timgortworst.roomy.domain.application.user.GetUserUseCase
import com.timgortworst.roomy.domain.application.user.RemoveUserUseCase
import com.timgortworst.roomy.domain.application.user.UserUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory<ErrorHandler> { ErrorHandlerImpl() }
    factory {
        TaskListUseCase(
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
    factory { EasterEggUseCase(get()) }
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
