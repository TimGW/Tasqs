package com.timgortworst.roomy.ui.housemates.module

import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.housemates.presenter.HousematesPresenter
import com.timgortworst.roomy.ui.housemates.view.HousematesFragment
import com.timgortworst.roomy.ui.housemates.view.HousenmatesView
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module

abstract class HousematesModule {

    @Binds
    internal abstract fun provideInviteView(inviteActivity: HousematesFragment): HousenmatesView

    @Module
    companion object {

        @Provides
        @JvmStatic
        internal fun provideInvitePresenter(
            inviteView: HousenmatesView,
            userRepository: UserRepository,
            sharedPref: HuishoudGenootSharedPref
        ): HousematesPresenter {
            return HousematesPresenter(inviteView, userRepository, sharedPref)
        }
    }
}
