package com.timgortworst.roomy.ui.setup.module

import com.timgortworst.roomy.repository.HouseholdRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.setup.presenter.SetupPresenter
import com.timgortworst.roomy.ui.setup.view.SetupActivity
import com.timgortworst.roomy.ui.setup.view.SetupView
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.InternalCoroutinesApi

@Module

abstract class SetupModule {

    @Binds
    internal abstract fun provideSetupView(setupActivity: SetupActivity): SetupView

    @Module
    companion object {

        @Provides
        @JvmStatic
        internal fun provideSetupPresenter(setupView: SetupView,
                                           householdRepository: HouseholdRepository,
                                           userRepository: UserRepository): SetupPresenter {
            return SetupPresenter(setupView, householdRepository, userRepository)
        }
    }
}
