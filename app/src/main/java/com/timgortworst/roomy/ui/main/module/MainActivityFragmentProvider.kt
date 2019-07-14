package com.timgortworst.roomy.ui.main.module

import com.timgortworst.roomy.ui.event.module.EventListModule
import com.timgortworst.roomy.ui.event.view.EventListFragment
import com.timgortworst.roomy.ui.category.module.CategoryListModule
import com.timgortworst.roomy.ui.category.view.CategoryListFragment
import com.timgortworst.roomy.ui.housemates.module.UserListModule
import com.timgortworst.roomy.ui.housemates.view.UserListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityFragmentProvider {

    @ContributesAndroidInjector(modules = [(CategoryListModule::class)])
    internal abstract fun provideTasksFragment(): CategoryListFragment

    @ContributesAndroidInjector(modules = [(EventListModule::class)])
    internal abstract fun provideAgendaFragment(): EventListFragment

    @ContributesAndroidInjector(modules = [(UserListModule::class)])
    internal abstract fun provideHouseMatesFragment(): UserListFragment
}
