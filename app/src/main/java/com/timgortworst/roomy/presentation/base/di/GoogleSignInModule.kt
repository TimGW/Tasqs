package com.timgortworst.roomy.presentation.base.di

import com.timgortworst.roomy.presentation.features.googlesignin.view.GoogleSignInActivity
import com.timgortworst.roomy.presentation.features.googlesignin.view.GoogleSignInView
import dagger.Binds
import dagger.Module

@Module
abstract class GoogleSignInModule {
    @Binds
    internal abstract fun provideSignInView(signInActivity: GoogleSignInActivity): GoogleSignInView
}
