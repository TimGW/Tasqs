package com.timgortworst.roomy.ui.category.module

import com.timgortworst.roomy.repository.AgendaRepository
import com.timgortworst.roomy.ui.category.presenter.CategoryListPresenter
import com.timgortworst.roomy.ui.category.view.CategoryListFragment
import com.timgortworst.roomy.ui.category.view.CategoryListView
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module

abstract class CategoryListModule {

    @Binds
    internal abstract fun provideMainTasksFragmentView(mainTasksFragment: CategoryListFragment): CategoryListView

    @Module
    companion object {

        @Provides
        @JvmStatic
        internal fun provideMainTasksPresenter(
                mainTasksFragmentView: CategoryListView,
                taskRepository: AgendaRepository
        ): CategoryListPresenter {
            return CategoryListPresenter(mainTasksFragmentView, taskRepository)
        }
    }
}
