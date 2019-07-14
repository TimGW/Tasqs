package com.timgortworst.roomy.ui.category.module

import com.timgortworst.roomy.repository.AgendaRepository
import com.timgortworst.roomy.ui.category.presenter.CategoryEditPresenter
import com.timgortworst.roomy.ui.category.view.CategoryEditActivity
import com.timgortworst.roomy.ui.category.view.CategoryEditView
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module

abstract class CategoryEditModule {

    @Binds
    internal abstract fun provideEditTaskView(editTaskActivity: CategoryEditActivity): CategoryEditView

    @Module
    companion object {
        @Provides
        @JvmStatic
        internal fun provideMainTasksPresenter(
                editTaskView: CategoryEditView,
                taskRepository: AgendaRepository
        ): CategoryEditPresenter {
            return CategoryEditPresenter(editTaskView, taskRepository)
        }
    }
}
