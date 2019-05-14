package com.timgortworst.roomy.ui.splash.module

import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import com.timgortworst.roomy.repository.HouseholdRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.splash.presenter.SplashPresenter
import com.timgortworst.roomy.ui.splash.ui.SplashActivity
import com.timgortworst.roomy.ui.splash.ui.SplashView
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.InternalCoroutinesApi

@Module

abstract class SplashModule {

    @Binds
    internal abstract fun provideSplashView(splashActivity: SplashActivity): SplashView

    @Module
    companion object {

        @Provides
        @JvmStatic
        internal fun provideSplashPresenter(
            splashView: SplashView,
            userRepository: UserRepository,
            auth: FirebaseAuth,
            householdRepository: HouseholdRepository,
            sharedPref: HuishoudGenootSharedPref): SplashPresenter {
            return SplashPresenter(splashView, householdRepository, userRepository, auth, sharedPref)
        }
    }
}
