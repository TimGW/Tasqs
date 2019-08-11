package com.timgortworst.roomy.ui.base.di

import com.timgortworst.roomy.ui.features.event.view.EventListFragment
import com.timgortworst.roomy.ui.features.event.view.EventListView
import dagger.Binds
import dagger.Module

@Module
abstract class EventListModule {
    @Binds
    internal abstract fun provideEventListView(mainAgendaFragment: EventListFragment): EventListView
}
