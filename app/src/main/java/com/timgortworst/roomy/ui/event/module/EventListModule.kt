package com.timgortworst.roomy.ui.event.module

import com.timgortworst.roomy.ui.event.view.EventListFragment
import com.timgortworst.roomy.ui.event.view.EventListView
import dagger.Binds
import dagger.Module

@Module
abstract class EventListModule {
    @Binds
    internal abstract fun provideEventListView(mainAgendaFragment: EventListFragment): EventListView
}
