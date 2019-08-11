package com.timgortworst.roomy.ui.base.di

import com.timgortworst.roomy.ui.features.splash.ui.SplashActivity
import com.timgortworst.roomy.ui.features.splash.ui.SplashView
import dagger.Binds
import dagger.Module

@Module
abstract class SplashModule {
    @Binds
    internal abstract fun provideSplashView(splashActivity: SplashActivity): SplashView
}
