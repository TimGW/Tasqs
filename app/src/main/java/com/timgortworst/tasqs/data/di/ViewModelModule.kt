package com.timgortworst.tasqs.data.di

import com.timgortworst.tasqs.presentation.features.main.MainViewModel
import com.timgortworst.tasqs.presentation.features.settings.SettingsViewModel
import com.timgortworst.tasqs.presentation.features.signin.SignInViewModel
import com.timgortworst.tasqs.presentation.features.splash.SplashViewModel
import com.timgortworst.tasqs.presentation.features.task.viewmodel.TaskEditViewModel
import com.timgortworst.tasqs.presentation.features.task.viewmodel.TaskInfoViewModel
import com.timgortworst.tasqs.presentation.features.task.viewmodel.TaskListViewModel
import com.timgortworst.tasqs.presentation.features.user.UserViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { TaskListViewModel(get(), get(), get(), get()) }
    viewModel { UserViewModel(get(), get(), get()) }
    viewModel { SettingsViewModel(get(), get(), get(), get()) }
    viewModel { SignInViewModel(get()) }
    viewModel { MainViewModel(get(), get()) }
    viewModel { SplashViewModel(get(), get(), get(), get()) }
    viewModel { TaskEditViewModel(get()) }
    viewModel { TaskInfoViewModel(get(), get(), get()) }
}
