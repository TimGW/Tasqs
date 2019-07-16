package com.timgortworst.roomy.di

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import com.timgortworst.roomy.RoomyApp
import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


/**
 * Created by tim.gortworst on 15/02/2018.
 *
 * Module for creating global objects required throughout the app added in @see[AppComponent]
 */
@Module

abstract class AppModule {

    @Binds
    internal abstract fun provideContext(application: Application): Context

    @Module
    companion object {
        val TAG = AppModule::class.java.simpleName

        @Provides
        @Singleton
        @JvmStatic
        fun provideSharedPreferences(): HuishoudGenootSharedPref {
            return HuishoudGenootSharedPref(
                PreferenceManager.getDefaultSharedPreferences(
                    RoomyApp.applicationContext()
                )
            )
        }
    }
}
