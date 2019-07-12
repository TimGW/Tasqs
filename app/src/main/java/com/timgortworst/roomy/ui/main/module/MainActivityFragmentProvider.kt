package com.timgortworst.roomy.ui.main.module

import com.timgortworst.roomy.ui.agenda.module.AgendaModule
import com.timgortworst.roomy.ui.agenda.view.AgendaFragment
import com.timgortworst.roomy.ui.eventcategory.module.EventCategoryModule
import com.timgortworst.roomy.ui.eventcategory.view.EventCategoryFragment
import com.timgortworst.roomy.ui.housemates.module.HousematesModule
import com.timgortworst.roomy.ui.housemates.view.HousematesFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityFragmentProvider {

    @ContributesAndroidInjector(modules = [(EventCategoryModule::class)])
    internal abstract fun provideTasksFragment(): EventCategoryFragment

    @ContributesAndroidInjector(modules = [(AgendaModule::class)])
    internal abstract fun provideAgendaFragment(): AgendaFragment

    @ContributesAndroidInjector(modules = [(HousematesModule::class)])
    internal abstract fun provideHouseMatesFragment(): HousematesFragment
}
