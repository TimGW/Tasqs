package com.timgortworst.roomy.ui.base.module

import dagger.Module
import kotlinx.coroutines.InternalCoroutinesApi

@Module

abstract class BaseAuthModule {

    // inject AuthRepo directly in activity and skip this boilerplate
//
//    @Binds
//    internal abstract fun provideBaseAuthView(profileActivity: BaseAuthActivity): BaseAuthView
//
//    @Module
//    companion object {
//
//        @Provides
//        @JvmStatic
//        internal fun provideBaseAuthPresenter(
//            profileView: BaseAuthView,
//            userRepository: UserRepository
//        ): BaseAuthPresenter {
//            return BaseAuthPresenter(profileView, userRepository)
//        }
//    }
}

