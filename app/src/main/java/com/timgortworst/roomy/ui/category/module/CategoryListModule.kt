package com.timgortworst.roomy.ui.category.module

import com.timgortworst.roomy.ui.category.view.CategoryListFragment
import com.timgortworst.roomy.ui.category.view.CategoryListView
import dagger.Binds
import dagger.Module

@Module
abstract class CategoryListModule {
    @Binds
    internal abstract fun provideCategoryListView(mainTasksFragment: CategoryListFragment): CategoryListView
}
