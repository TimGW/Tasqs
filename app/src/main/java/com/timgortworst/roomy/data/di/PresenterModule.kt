package com.timgortworst.roomy.data.di


import com.timgortworst.roomy.presentation.features.auth.AuthCallback
import com.timgortworst.roomy.presentation.features.event.presenter.EventEditPresenter
import com.timgortworst.roomy.presentation.features.event.presenter.EventInfoPresenter
import com.timgortworst.roomy.presentation.features.event.presenter.EventListPresenter
import com.timgortworst.roomy.presentation.features.event.view.EventEditView
import com.timgortworst.roomy.presentation.features.event.view.EventInfoView
import com.timgortworst.roomy.presentation.features.event.view.EventListView
import com.timgortworst.roomy.presentation.features.main.MainPresenter
import com.timgortworst.roomy.presentation.features.main.MainView
import com.timgortworst.roomy.presentation.features.onboarding.OnboardingPresenter
import com.timgortworst.roomy.presentation.features.settings.SettingsPresenter
import com.timgortworst.roomy.presentation.features.settings.SettingsView
import com.timgortworst.roomy.presentation.features.splash.SplashPresenter
import com.timgortworst.roomy.presentation.features.splash.SplashView
import com.timgortworst.roomy.presentation.features.user.UserListPresenter
import com.timgortworst.roomy.presentation.features.user.UserListView
import org.koin.dsl.module

val presenterModule = module(override = true) {
    factory { (view: SplashView) -> SplashPresenter(view, get(), get()) }
    factory { (view: AuthCallback) -> OnboardingPresenter(view, get(), get(), get()) }
    factory { (view: MainView) -> MainPresenter(view, get(), get(), get()) }
    factory { (view: EventListView) -> EventListPresenter(view) }
    factory { (view: EventInfoView) -> EventInfoPresenter(view) }
    factory { (view: EventEditView) -> EventEditPresenter(view, get(),get()) }
    factory { (view: UserListView) -> UserListPresenter(view, get()) }
    factory { (view: SettingsView) -> SettingsPresenter(view, get()) }
}
