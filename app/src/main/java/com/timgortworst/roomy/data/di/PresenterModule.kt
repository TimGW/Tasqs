package com.timgortworst.roomy.data.di

import com.timgortworst.roomy.presentation.features.settings.SettingsPresenter
import com.timgortworst.roomy.presentation.features.settings.SettingsView
import com.timgortworst.roomy.presentation.features.splash.SplashPresenter
import com.timgortworst.roomy.presentation.features.splash.SplashView
import com.timgortworst.roomy.presentation.features.task.presenter.TaskEditPresenter
import com.timgortworst.roomy.presentation.features.task.view.TaskEditView
import org.koin.dsl.module

val presenterModule = module {
    factory { (view: SplashView) -> SplashPresenter(view, get()) }
    factory { (view: TaskEditView) -> TaskEditPresenter(view, get(),get()) }
    factory { (view: SettingsView) -> SettingsPresenter(view, get()) }
}
