package com.timgortworst.roomy.ui.users.module

import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.users.presenter.UsersPresenter
import com.timgortworst.roomy.ui.users.view.UsersActivity
import com.timgortworst.roomy.ui.users.view.UsersView
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.InternalCoroutinesApi

@Module

abstract class UsersModule {

    @Binds
    internal abstract fun provideInviteView(inviteActivity: UsersActivity): UsersView

    @Module
    companion object {

        @Provides
        @JvmStatic
        internal fun provideInvitePresenter(
                inviteView: UsersView,
                userRepository: UserRepository,
                sharedPref: HuishoudGenootSharedPref
        ): UsersPresenter {
            return UsersPresenter(inviteView, userRepository, sharedPref)
        }
    }
}
