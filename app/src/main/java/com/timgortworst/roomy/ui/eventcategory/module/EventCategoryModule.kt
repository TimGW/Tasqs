package com.timgortworst.roomy.ui.eventcategory.module

import com.timgortworst.roomy.repository.AgendaRepository
import com.timgortworst.roomy.ui.eventcategory.presenter.EventCategoryPresenter
import com.timgortworst.roomy.ui.eventcategory.view.EventCategoryFragment
import com.timgortworst.roomy.ui.eventcategory.view.EventCategoryFragmentView
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.InternalCoroutinesApi

@Module

abstract class EventCategoryModule {

    @Binds
    internal abstract fun provideMainTasksFragmentView(mainTasksFragment: EventCategoryFragment): EventCategoryFragmentView

    @Module
    companion object {

        @Provides
        @JvmStatic
        internal fun provideMainTasksPresenter(
            mainTasksFragmentView: EventCategoryFragmentView,
            taskRepository: AgendaRepository
        ): EventCategoryPresenter {
            return EventCategoryPresenter(mainTasksFragmentView, taskRepository)
        }
    }
}
