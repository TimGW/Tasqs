package com.timgortworst.roomy.ui.features.main.module

import com.timgortworst.roomy.ui.features.category.module.CategoryListModule
import com.timgortworst.roomy.ui.features.category.view.CategoryListFragment
import com.timgortworst.roomy.ui.features.event.module.EventListModule
import com.timgortworst.roomy.ui.features.event.view.EventListFragment
import com.timgortworst.roomy.ui.features.main.view.MainActivity
import com.timgortworst.roomy.ui.features.main.view.MainView
import com.timgortworst.roomy.ui.features.user.module.UserListModule
import com.timgortworst.roomy.ui.features.user.view.UserListFragment
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainModule {

    @Binds
    internal abstract fun provideMainView(mainActivity: MainActivity): MainView

    @ContributesAndroidInjector(modules = [(EventListModule::class)])
    internal abstract fun provideAgendaFragment(): EventListFragment

    @ContributesAndroidInjector(modules = [(CategoryListModule::class)])
    internal abstract fun provideTasksFragment(): CategoryListFragment

    @ContributesAndroidInjector(modules = [(UserListModule::class)])
    internal abstract fun provideHouseMatesFragment(): UserListFragment
}
