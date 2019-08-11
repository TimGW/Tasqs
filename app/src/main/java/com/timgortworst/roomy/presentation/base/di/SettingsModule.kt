package com.timgortworst.roomy.presentation.base.di

import com.timgortworst.roomy.presentation.features.settings.view.SettingsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class SettingsModule {
    @ContributesAndroidInjector
    internal abstract fun provideSettingsFragment(): SettingsFragment
}