package com.timgortworst.roomy.ui.housemates.module

import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.housemates.presenter.UserListPresenter
import com.timgortworst.roomy.ui.housemates.view.UserListFragment
import com.timgortworst.roomy.ui.housemates.view.UserListView
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module

abstract class UserListModule {

    @Binds
    internal abstract fun provideInviteView(inviteActivity: UserListFragment): UserListView

    @Module
    companion object {

        @Provides
        @JvmStatic
        internal fun provideInvitePresenter(
            inviteView: UserListView,
            userRepository: UserRepository,
            sharedPref: HuishoudGenootSharedPref
        ): UserListPresenter {
            return UserListPresenter(inviteView, userRepository, sharedPref)
        }
    }
}
