package com.timgortworst.roomy.data.di


import com.timgortworst.roomy.presentation.features.auth.AuthCallback
import com.timgortworst.roomy.presentation.features.task.presenter.TaskEditPresenter
import com.timgortworst.roomy.presentation.features.task.presenter.TaskInfoPresenter
import com.timgortworst.roomy.presentation.features.task.presenter.TaskListPresenter
import com.timgortworst.roomy.presentation.features.task.view.TaskEditView
import com.timgortworst.roomy.presentation.features.task.view.TaskInfoView
import com.timgortworst.roomy.presentation.features.task.view.TaskListView
import com.timgortworst.roomy.presentation.features.main.MainPresenter
import com.timgortworst.roomy.presentation.features.main.MainView
import com.timgortworst.roomy.presentation.features.onboarding.OnboardingPresenter
import com.timgortworst.roomy.presentation.features.settings.SettingsPresenter
import com.timgortworst.roomy.presentation.features.settings.SettingsView
import com.timgortworst.roomy.presentation.features.splash.SplashPresenter
import com.timgortworst.roomy.presentation.features.splash.SplashView
import org.koin.dsl.module

val presenterModule = module {
    factory { (view: SplashView) -> SplashPresenter(view, get(), get()) }
    factory { (view: AuthCallback) -> OnboardingPresenter(view, get(), get(), get()) }
    factory { (view: MainView) -> MainPresenter(view, get(), get(), get()) }
    factory { (view: TaskListView) -> TaskListPresenter(view) }
    factory { (view: TaskInfoView) -> TaskInfoPresenter(view) }
    factory { (view: TaskEditView) -> TaskEditPresenter(view, get(),get()) }
    factory { (view: SettingsView) -> SettingsPresenter(view, get()) }
}
