package com.timgortworst.roomy.di

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module


/**
 * Created by tim.gortworst on 15/02/2018.
 *
 * Module for creating global complex objects required throughout the app added in @see[AppComponent]
 */
@Module

abstract class AppModule {

    @Binds
    internal abstract fun provideContext(application: Application): Context
}
