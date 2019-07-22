package com.timgortworst.roomy.ui.splash.module

import com.timgortworst.roomy.ui.splash.ui.SplashActivity
import com.timgortworst.roomy.ui.splash.ui.SplashView
import dagger.Binds
import dagger.Module

@Module
abstract class SplashModule {
    @Binds
    internal abstract fun provideSplashView(splashActivity: SplashActivity): SplashView
}
