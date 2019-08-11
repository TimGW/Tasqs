package com.timgortworst.roomy.ui.base.di

import com.timgortworst.roomy.ui.features.splash.view.SplashActivity
import com.timgortworst.roomy.ui.features.splash.view.SplashView
import dagger.Binds
import dagger.Module

@Module
abstract class SplashModule {
    @Binds
    internal abstract fun provideSplashView(splashActivity: SplashActivity): SplashView
}
