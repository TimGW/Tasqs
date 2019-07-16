package com.timgortworst.roomy.ui.user.module

import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.user.presenter.UserListPresenter
import com.timgortworst.roomy.ui.user.view.UserListFragment
import com.timgortworst.roomy.ui.user.view.UserListView
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
            userRepository: UserRepository): UserListPresenter {
            return UserListPresenter(inviteView, userRepository)
        }
    }
}
