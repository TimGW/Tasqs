package com.timgortworst.roomy.ui.eventcategory.module

import com.timgortworst.roomy.repository.AgendaRepository
import com.timgortworst.roomy.ui.eventcategory.presenter.EditEventCategoryPresenter
import com.timgortworst.roomy.ui.eventcategory.view.EditEventCategoryActivity
import com.timgortworst.roomy.ui.eventcategory.view.EditEventCategoryView
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.InternalCoroutinesApi

@Module

abstract class EditEventCategoryModule {

    @Binds
    internal abstract fun provideEditTaskView(editTaskActivity: EditEventCategoryActivity): EditEventCategoryView

    @Module
    companion object {
        @Provides
        @JvmStatic
        internal fun provideMainTasksPresenter(
            editTaskView: EditEventCategoryView,
            taskRepository: AgendaRepository
        ): EditEventCategoryPresenter {
            return EditEventCategoryPresenter(editTaskView, taskRepository)
        }
    }
}
