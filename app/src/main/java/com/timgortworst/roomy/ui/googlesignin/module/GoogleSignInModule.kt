package com.timgortworst.roomy.ui.googlesignin.module

import com.timgortworst.roomy.ui.googlesignin.view.GoogleSignInActivity
import com.timgortworst.roomy.ui.googlesignin.view.GoogleSignInView
import dagger.Binds
import dagger.Module

@Module
abstract class GoogleSignInModule {
    @Binds
    internal abstract fun provideSignInView(signInActivity: GoogleSignInActivity): GoogleSignInView
}
