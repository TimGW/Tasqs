package com.timgortworst.roomy.presentation.base.di

import com.timgortworst.roomy.presentation.features.event.view.EventListFragment
import com.timgortworst.roomy.presentation.features.event.view.EventListView
import dagger.Binds
import dagger.Module

@Module
abstract class EventListModule {
    @Binds
    internal abstract fun provideEventListView(mainAgendaFragment: EventListFragment): EventListView
}
