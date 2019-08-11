package com.timgortworst.roomy.data.di


import com.timgortworst.roomy.ui.base.di.CategoryEditModule
import com.timgortworst.roomy.ui.features.category.view.CategoryEditActivity
import com.timgortworst.roomy.ui.base.di.EventEditModule
import com.timgortworst.roomy.ui.features.event.view.EventEditActivity
import com.timgortworst.roomy.ui.base.di.GoogleSignInModule
import com.timgortworst.roomy.ui.features.googlesignin.view.GoogleSignInActivity
import com.timgortworst.roomy.ui.base.di.MainModule
import com.timgortworst.roomy.ui.features.main.view.MainActivity
import com.timgortworst.roomy.ui.base.di.SettingsModule
import com.timgortworst.roomy.ui.features.settings.view.SettingsActivity
import com.timgortworst.roomy.ui.base.di.SetupModule
import com.timgortworst.roomy.ui.features.setup.view.SetupActivity
import com.timgortworst.roomy.ui.base.di.SplashModule
import com.timgortworst.roomy.ui.features.splash.ui.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Module for building the main/global Activities added in @see[AppComponent]
 */
@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [MainModule::class])
    internal abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [CategoryEditModule::class])
    internal abstract fun bindEditTaskActivity(): CategoryEditActivity

    @ContributesAndroidInjector(modules = [EventEditModule::class])
    internal abstract fun bindEditAgendaEventActivity(): EventEditActivity

    @ContributesAndroidInjector(modules = [GoogleSignInModule::class])
    internal abstract fun bindSignInActivity(): GoogleSignInActivity

    @ContributesAndroidInjector(modules = [SetupModule::class])
    internal abstract fun bindSetupActivity(): SetupActivity

    @ContributesAndroidInjector(modules = [SplashModule::class])
    internal abstract fun bindSplashActivity(): SplashActivity

    @ContributesAndroidInjector(modules = [SettingsModule::class])
    internal abstract fun bindSettingsActivity(): SettingsActivity
}
