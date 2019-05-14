package com.timgortworst.roomy.ui.main.module

import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import com.timgortworst.roomy.repository.HouseholdRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.main.presenter.MainPresenter
import com.timgortworst.roomy.ui.main.view.MainActivity
import com.timgortworst.roomy.ui.main.view.MainView
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.InternalCoroutinesApi

@Module

abstract class MainActivityModule {

    @Binds
    internal abstract fun provideMainView(mainActivity: MainActivity): MainView

    @Module
    companion object {

        @Provides
        @JvmStatic
        internal fun provideMainPresenter(
            mainView: MainView,
            policeSharedPreferences: HuishoudGenootSharedPref,
            householdRepository: HouseholdRepository,
            userRepository: UserRepository
        ): MainPresenter {
            return MainPresenter(mainView, policeSharedPreferences, householdRepository, userRepository)
        }
    }
}
