package com.timgortworst.roomy.ui.base.di

import com.timgortworst.roomy.ui.features.settings.view.SettingsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class SettingsModule {
    @ContributesAndroidInjector
    internal abstract fun provideSettingsFragment(): SettingsFragment
}