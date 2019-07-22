package com.timgortworst.roomy.ui.user.module

import com.timgortworst.roomy.ui.user.view.UserListFragment
import com.timgortworst.roomy.ui.user.view.UserListView
import dagger.Binds
import dagger.Module

@Module
abstract class UserListModule {
    @Binds
    internal abstract fun provideInviteView(inviteActivity: UserListFragment): UserListView
}
