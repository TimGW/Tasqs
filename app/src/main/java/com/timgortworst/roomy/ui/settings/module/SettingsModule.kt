package com.timgortworst.roomy.ui.settings.module

import com.timgortworst.roomy.ui.settings.view.SettingsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class SettingsModule {
    @ContributesAndroidInjector
    internal abstract fun provideSettingsFragment(): SettingsFragment
}