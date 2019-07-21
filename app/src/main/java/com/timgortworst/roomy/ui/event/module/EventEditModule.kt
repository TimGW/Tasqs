package com.timgortworst.roomy.ui.event.module

import com.timgortworst.roomy.repository.CategoryRepository
import com.timgortworst.roomy.repository.EventRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.event.presenter.EventEditPresenter
import com.timgortworst.roomy.ui.event.view.EventEditActivity
import com.timgortworst.roomy.ui.event.view.EventEditView
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module

abstract class EventEditModule {

    @Binds
    internal abstract fun provideEventEditView(editAgendaEventActivity: EventEditActivity): EventEditView

    @Module
    companion object {

        @Provides
        @JvmStatic
        internal fun provideEventEditPresenter(
            view: EventEditView,
            agendaRepository: EventRepository,
            userRepository: UserRepository,
            categoryRepository: CategoryRepository
        ): EventEditPresenter {
            return EventEditPresenter(view, agendaRepository, userRepository, categoryRepository)
        }
    }
}
