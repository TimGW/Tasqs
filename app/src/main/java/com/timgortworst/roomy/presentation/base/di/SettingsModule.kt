package com.timgortworst.roomy.presentation.base.di

import com.timgortworst.roomy.presentation.features.settings.view.SettingsFragment
import com.timgortworst.roomy.presentation.features.settings.view.SettingsView
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class SettingsModule {

    @ContributesAndroidInjector(modules = [(SettingsFragmentModule::class)])
    internal abstract fun provideSettingsFragment(): SettingsFragment

    @Module
    abstract class SettingsFragmentModule {

        @Binds
        internal abstract fun provideSetupView(settingsFragment: SettingsFragment) : SettingsView
    }
}