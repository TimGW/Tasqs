package com.timgortworst.roomy.ui.main.module

import com.timgortworst.roomy.ui.event.module.EventListModule
import com.timgortworst.roomy.ui.event.view.EventListFragment
import com.timgortworst.roomy.ui.category.module.CategoryListModule
import com.timgortworst.roomy.ui.category.view.CategoryListFragment
import com.timgortworst.roomy.ui.housemates.module.HousematesModule
import com.timgortworst.roomy.ui.housemates.view.HousematesFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityFragmentProvider {

    @ContributesAndroidInjector(modules = [(CategoryListModule::class)])
    internal abstract fun provideTasksFragment(): CategoryListFragment

    @ContributesAndroidInjector(modules = [(EventListModule::class)])
    internal abstract fun provideAgendaFragment(): EventListFragment

    @ContributesAndroidInjector(modules = [(HousematesModule::class)])
    internal abstract fun provideHouseMatesFragment(): HousematesFragment
}
