package com.timgortworst.roomy.presentation.base.di

import com.timgortworst.roomy.presentation.features.user.view.UserListFragment
import com.timgortworst.roomy.presentation.features.user.view.UserListView
import dagger.Binds
import dagger.Module

@Module
abstract class UserListModule {
    @Binds
    internal abstract fun provideInviteView(inviteActivity: UserListFragment): UserListView
}
