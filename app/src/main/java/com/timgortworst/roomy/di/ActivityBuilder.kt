package com.timgortworst.roomy.di


import com.timgortworst.roomy.ui.base.view.BaseAuthActivity
import com.timgortworst.roomy.ui.agenda.module.EditAgendaEventModule
import com.timgortworst.roomy.ui.agenda.view.EditAgendaEventActivity
import com.timgortworst.roomy.ui.base.module.BaseAuthModule
import com.timgortworst.roomy.ui.eventcategory.module.EditEventCategoryModule
import com.timgortworst.roomy.ui.eventcategory.view.EditEventCategoryActivity
import com.timgortworst.roomy.ui.main.module.MainActivityFragmentProvider
import com.timgortworst.roomy.ui.main.module.MainActivityModule
import com.timgortworst.roomy.ui.main.view.MainActivity
import com.timgortworst.roomy.ui.profile.module.ProfileModule
import com.timgortworst.roomy.ui.profile.view.ProfileActivity
import com.timgortworst.roomy.ui.setup.module.SetupModule
import com.timgortworst.roomy.ui.setup.view.SetupActivity
import com.timgortworst.roomy.ui.googlesignin.module.GoogleSignInModule
import com.timgortworst.roomy.ui.googlesignin.view.GoogleSignInActivity
import com.timgortworst.roomy.ui.settings.module.SettingsActivityFragmentProvider
import com.timgortworst.roomy.ui.settings.view.SettingsActivity
import com.timgortworst.roomy.ui.splash.module.SplashModule
import com.timgortworst.roomy.ui.splash.ui.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Module for building the main/global Activities added in @see[AppComponent]
 */
@Module

abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [(MainActivityModule::class), MainActivityFragmentProvider::class])
    internal abstract fun bindMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [EditEventCategoryModule::class])
    internal abstract fun bindEditTaskActivity(): EditEventCategoryActivity

    @ContributesAndroidInjector(modules = [EditAgendaEventModule::class])
    internal abstract fun bindEditAgendaEventActivity(): EditAgendaEventActivity

    @ContributesAndroidInjector(modules = [GoogleSignInModule::class])
    internal abstract fun bindSignInActivity(): GoogleSignInActivity

    @ContributesAndroidInjector(modules = [ProfileModule::class])
    internal abstract fun bindProfileActivity(): ProfileActivity

    @ContributesAndroidInjector(modules = [SetupModule::class])
    internal abstract fun bindSetupActivity(): SetupActivity

    @ContributesAndroidInjector(modules = [SplashModule::class])
    internal abstract fun bindSplashActivity(): SplashActivity

    @ContributesAndroidInjector(modules = [BaseAuthModule::class])
    internal abstract fun bindBaseAuthActivity(): BaseAuthActivity

    @ContributesAndroidInjector(modules = [SettingsActivityFragmentProvider::class])
    internal abstract fun bindSettingsActivity(): SettingsActivity
}
