package com.timgortworst.roomy.di


import com.timgortworst.roomy.ui.category.module.CategoryEditModule
import com.timgortworst.roomy.ui.category.view.CategoryEditActivity
import com.timgortworst.roomy.ui.event.module.EventEditModule
import com.timgortworst.roomy.ui.event.view.EventEditActivity
import com.timgortworst.roomy.ui.googlesignin.module.GoogleSignInModule
import com.timgortworst.roomy.ui.googlesignin.view.GoogleSignInActivity
import com.timgortworst.roomy.ui.main.module.MainModule
import com.timgortworst.roomy.ui.main.view.MainActivity
import com.timgortworst.roomy.ui.settings.module.SettingsActivityFragmentProvider
import com.timgortworst.roomy.ui.settings.view.SettingsActivity
import com.timgortworst.roomy.ui.setup.module.SetupModule
import com.timgortworst.roomy.ui.setup.view.SetupActivity
import com.timgortworst.roomy.ui.splash.module.SplashModule
import com.timgortworst.roomy.ui.splash.ui.SplashActivity
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

    @ContributesAndroidInjector(modules = [SettingsActivityFragmentProvider::class])
    internal abstract fun bindSettingsActivity(): SettingsActivity
}
