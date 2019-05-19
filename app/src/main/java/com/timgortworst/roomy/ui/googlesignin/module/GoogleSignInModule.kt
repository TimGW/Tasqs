package com.timgortworst.roomy.ui.googlesignin.module

import com.timgortworst.roomy.repository.AuthRepository
import com.timgortworst.roomy.ui.googlesignin.presenter.GoogleSignInPresenter
import com.timgortworst.roomy.ui.googlesignin.view.GoogleSignInActivity
import com.timgortworst.roomy.ui.googlesignin.view.GoogleSignInView
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module

abstract class GoogleSignInModule {

    @Binds
    internal abstract fun provideSignInView(signInActivity: GoogleSignInActivity): GoogleSignInView

    @Module
    companion object {

        @Provides
        @JvmStatic
        internal fun provideSignInPresenter(
            signInView: GoogleSignInView,
            authRepository: AuthRepository
        ): GoogleSignInPresenter {
            return GoogleSignInPresenter(signInView, authRepository)
        }
    }
}
