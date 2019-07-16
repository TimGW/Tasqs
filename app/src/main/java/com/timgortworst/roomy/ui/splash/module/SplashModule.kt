package com.timgortworst.roomy.ui.splash.module

import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.splash.presenter.SplashPresenter
import com.timgortworst.roomy.ui.splash.ui.SplashActivity
import com.timgortworst.roomy.ui.splash.ui.SplashView
import dagger.Binds
import dagger.Module
import dagger.Provides

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
            userRepository: UserRepository
        ): SplashPresenter {
            return SplashPresenter(splashView, userRepository)
        }
    }
}
