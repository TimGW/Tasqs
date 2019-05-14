package com.timgortworst.roomy.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import com.timgortworst.roomy.repository.AgendaRepository
import com.timgortworst.roomy.repository.AuthRepository
import com.timgortworst.roomy.repository.HouseholdRepository
import com.timgortworst.roomy.repository.UserRepository
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.InternalCoroutinesApi


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
        internal fun provideSignInRepository(
            database: FirebaseFirestore,
            auth: FirebaseAuth,
            sharedPref: HuishoudGenootSharedPref
        ): UserRepository {
            return UserRepository(database, auth, sharedPref)
        }

        @Provides
        @JvmStatic
        internal fun provideHouseholdRepository(
            database: FirebaseFirestore,
            sharedPref: HuishoudGenootSharedPref,
            auth: FirebaseAuth): HouseholdRepository {
            return HouseholdRepository(database, sharedPref, auth)
        }

        @Provides
        @JvmStatic
        internal fun provideTaskRepository(
            database: FirebaseFirestore,
            sharedPref: HuishoudGenootSharedPref
        ): AgendaRepository {
            return AgendaRepository(database, sharedPref)
        }

        @Provides
        @JvmStatic
        internal fun provideAuthRepository(
            database: FirebaseFirestore,
            sharedPref: HuishoudGenootSharedPref,
            auth: FirebaseAuth
        ): AuthRepository {
            return AuthRepository(database, sharedPref, auth)
        }
    }
}
