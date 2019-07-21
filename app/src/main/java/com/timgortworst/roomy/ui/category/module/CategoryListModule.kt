package com.timgortworst.roomy.ui.category.module

import com.timgortworst.roomy.repository.CategoryRepository
import com.timgortworst.roomy.ui.category.presenter.CategoryListPresenter
import com.timgortworst.roomy.ui.category.view.CategoryListFragment
import com.timgortworst.roomy.ui.category.view.CategoryListView
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module

abstract class CategoryListModule {

    @Binds
    internal abstract fun provideCategoryListView(mainTasksFragment: CategoryListFragment): CategoryListView

    @Module
    companion object {

        @Provides
        @JvmStatic
        internal fun provideCategoryListPresenter(
            categoryListView: CategoryListView,
            categoryRepository: CategoryRepository
        ): CategoryListPresenter {
            return CategoryListPresenter(categoryListView, categoryRepository)
        }
    }
}
