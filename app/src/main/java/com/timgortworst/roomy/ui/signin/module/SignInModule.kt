package com.timgortworst.roomy.ui.signin.module

import com.timgortworst.roomy.repository.AuthRepository
import com.timgortworst.roomy.repository.HouseholdRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.signin.presenter.SignInPresenter
import com.timgortworst.roomy.ui.signin.view.SignInActivity
import com.timgortworst.roomy.ui.signin.view.SignInView
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.InternalCoroutinesApi

@Module

abstract class SignInModule {

    @Binds
    internal abstract fun provideSignInView(signInActivity: SignInActivity): SignInView

    @Module
    companion object {

        @Provides
        @JvmStatic
        internal fun provideSignInPresenter(
            signInView: SignInView,
            userRepository: UserRepository,
            householdRepository: HouseholdRepository,
            authRepository: AuthRepository
        ): SignInPresenter {
            return SignInPresenter(signInView, userRepository, householdRepository, authRepository)
        }
    }
}
