package com.timgortworst.roomy.data.di

import com.timgortworst.roomy.presentation.features.main.MainViewModel
import com.timgortworst.roomy.presentation.features.settings.SettingsViewModel
import com.timgortworst.roomy.presentation.features.signin.SignInViewModel
import com.timgortworst.roomy.presentation.features.splash.SplashViewModel
import com.timgortworst.roomy.presentation.features.task.viewmodel.TaskEditViewModel
import com.timgortworst.roomy.presentation.features.task.viewmodel.TaskListViewModel
import com.timgortworst.roomy.presentation.features.user.UserViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        TaskListViewModel(
            get(named("CompleteTaskUseCase")),
            get(named("DeleteTaskUseCase")),
            get(named("GetAllTasksUseCase")),
            get(named("GetTasksForUserUseCase"))
        )
    }
    viewModel {
        UserViewModel(
            get(named("RemoveUserUseCase")),
            get(named("GetAllTasksUseCase"))
        )
    }
    viewModel {
        SettingsViewModel(
            get(named("EasterEggUseCase")),
            get(named("GetUserUseCase"))
        )
    }
    viewModel {
        SignInViewModel(
            get(named("SignInUseCase"))
        )
    }
    viewModel {
        MainViewModel(
            get(named("AdsVisibleUseCase")),
            get(named("GetUserUseCase"))
        )
    }
    viewModel {
        SplashViewModel(
            get(named("SwitchHouseholdUseCase")),
            get(named("ValidationUseCase"))
        )
    }
    viewModel {
        TaskEditViewModel(
            get(named("CreateOrUpdateTaskUseCase")),
            get(named("GetFbUserUseCase")),
            get(named("GetAllUsersUseCase"))
        )
    }
}
