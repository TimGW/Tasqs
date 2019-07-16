package com.timgortworst.roomy.ui.settings.module

import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.settings.presenter.SettingsPresenter
import com.timgortworst.roomy.ui.settings.view.SettingsFragment
import com.timgortworst.roomy.ui.settings.view.SettingsView
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module

abstract class SettingsModule {

    @Binds
    internal abstract fun provideSettingsView(splashActivity: SettingsFragment): SettingsView

    @Module
    companion object {

        @Provides
        @JvmStatic
        internal fun provideSettingsPresenter(
            settingsView: SettingsView,
            userRepository: UserRepository): SettingsPresenter {
            return SettingsPresenter(settingsView, userRepository)
        }
    }
}
