package com.timgortworst.roomy.data.di

import com.timgortworst.roomy.presentation.features.task.viewmodel.TaskViewModel
import com.timgortworst.roomy.presentation.features.user.UserViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { TaskViewModel(get()) }
    viewModel { UserViewModel(get()) }
}
