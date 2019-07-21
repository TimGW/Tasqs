package com.timgortworst.roomy.ui.event.module

import com.timgortworst.roomy.repository.EventRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.event.presenter.EventListPresenter
import com.timgortworst.roomy.ui.event.view.EventListFragment
import com.timgortworst.roomy.ui.event.view.EventListView
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module

abstract class EventListModule {

    @Binds
    internal abstract fun provideEventListView(mainAgendaFragment: EventListFragment): EventListView

    @Module
    companion object {

        @Provides
        @JvmStatic
        internal fun provideEventListPresenter(
            mainAgendaFragmentView: EventListView,
            agendaRepository: EventRepository,
            userRepository: UserRepository
        ): EventListPresenter {
            return EventListPresenter(mainAgendaFragmentView, agendaRepository, userRepository)
        }
    }
}
