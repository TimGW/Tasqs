package com.timgortworst.roomy.ui.main.module

import com.timgortworst.roomy.ui.agenda.module.AgendaModule
import com.timgortworst.roomy.ui.agenda.ui.AgendaFragment
import com.timgortworst.roomy.ui.eventcategory.module.EventCategoryModule
import com.timgortworst.roomy.ui.eventcategory.view.EventCategoryFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector
import kotlinx.coroutines.InternalCoroutinesApi

@Module

abstract class MainActivityFragmentProvider {

    @ContributesAndroidInjector(modules = [(EventCategoryModule::class)])
    internal abstract fun provideTasksFragment(): EventCategoryFragment

    @ContributesAndroidInjector(modules = [(AgendaModule::class)])
    internal abstract fun provideAgendaFragment(): AgendaFragment
}
