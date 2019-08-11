package com.timgortworst.roomy.presentation.base.di

import com.timgortworst.roomy.presentation.features.category.view.CategoryListFragment
import com.timgortworst.roomy.presentation.features.event.view.EventListFragment
import com.timgortworst.roomy.presentation.features.main.view.MainActivity
import com.timgortworst.roomy.presentation.features.main.view.MainView
import com.timgortworst.roomy.presentation.features.user.view.UserListFragment
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
