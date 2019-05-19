package com.timgortworst.roomy.ui.profile.module

import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import com.timgortworst.roomy.repository.HouseholdRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.profile.presenter.ProfilePresenter
import com.timgortworst.roomy.ui.profile.view.ProfileActivity
import com.timgortworst.roomy.ui.profile.view.ProfileView
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.InternalCoroutinesApi

@Module

abstract class ProfileModule {

    @Binds
    internal abstract fun provideProfileView(profileActivity: ProfileActivity): ProfileView

    @Module
    companion object {

        @Provides
        @JvmStatic
        internal fun provideProfilePresenter(
            profileView: ProfileView,
            userRepository: UserRepository,
            householdRepository: HouseholdRepository,
            sharedPref: HuishoudGenootSharedPref
        ): ProfilePresenter {
            return ProfilePresenter(profileView, userRepository, householdRepository, sharedPref)
        }
    }
}
