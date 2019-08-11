package com.timgortworst.roomy.presentation.base.di

import com.timgortworst.roomy.presentation.features.setup.view.SetupActivity
import com.timgortworst.roomy.presentation.features.setup.view.SetupView
import dagger.Binds
import dagger.Module

@Module
abstract class SetupModule {
    @Binds
    internal abstract fun provideSetupView(setupActivity: SetupActivity): SetupView
}
