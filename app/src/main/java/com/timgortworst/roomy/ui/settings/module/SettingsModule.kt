package com.timgortworst.roomy.ui.settings.module

import com.timgortworst.roomy.ui.settings.view.SettingsFragment
import com.timgortworst.roomy.ui.settings.view.SettingsView
import dagger.Binds
import dagger.Module

@Module
abstract class SettingsModule {
    @Binds
    internal abstract fun provideSettingsView(splashActivity: SettingsFragment): SettingsView
}
