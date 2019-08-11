package com.timgortworst.roomy.ui.base.di

import com.timgortworst.roomy.ui.features.user.view.UserListFragment
import com.timgortworst.roomy.ui.features.user.view.UserListView
import dagger.Binds
import dagger.Module

@Module
abstract class UserListModule {
    @Binds
    internal abstract fun provideInviteView(inviteActivity: UserListFragment): UserListView
}
