package com.timgortworst.roomy.ui.setup.module

import com.timgortworst.roomy.ui.setup.view.SetupActivity
import com.timgortworst.roomy.ui.setup.view.SetupView
import dagger.Binds
import dagger.Module

@Module
abstract class SetupModule {
    @Binds
    internal abstract fun provideSetupView(setupActivity: SetupActivity): SetupView
}
