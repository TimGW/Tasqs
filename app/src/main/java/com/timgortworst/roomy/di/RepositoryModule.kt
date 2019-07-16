package com.timgortworst.roomy.di

import com.google.firebase.firestore.FirebaseFirestore
import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import com.timgortworst.roomy.repository.AgendaRepository
import com.timgortworst.roomy.repository.HouseholdRepository
import com.timgortworst.roomy.repository.UserRepository
import dagger.Module
import dagger.Provides


/**
 * Created by tim.gortworst on 15/02/2018.
 *
 * Module for creating global objects required throughout the app added in @see[AppComponent]
 */
@Module

abstract class RepositoryModule {

    @Module
    companion object {
        @Provides
        @JvmStatic
        internal fun provideSignInRepository(): UserRepository {
            return UserRepository()
        }

        @Provides
        @JvmStatic
        internal fun provideHouseholdRepository(): HouseholdRepository {
            return HouseholdRepository()
        }

        @Provides
        @JvmStatic
        internal fun provideTaskRepository(
            userRepository: UserRepository
        ): AgendaRepository {
            return AgendaRepository(userRepository)
        }
    }
}
