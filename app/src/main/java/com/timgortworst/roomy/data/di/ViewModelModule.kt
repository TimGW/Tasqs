package com.timgortworst.roomy.data.di

import com.timgortworst.roomy.presentation.features.event.viewmodel.EventViewModel
import com.timgortworst.roomy.presentation.features.user.UserViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { EventViewModel(get()) }
    viewModel { UserViewModel(get()) }
}
