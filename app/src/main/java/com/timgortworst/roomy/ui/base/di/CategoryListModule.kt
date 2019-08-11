package com.timgortworst.roomy.ui.base.di

import com.timgortworst.roomy.ui.features.category.view.CategoryListFragment
import com.timgortworst.roomy.ui.features.category.view.CategoryListView
import dagger.Binds
import dagger.Module

@Module
abstract class CategoryListModule {
    @Binds
    internal abstract fun provideCategoryListView(mainTasksFragment: CategoryListFragment): CategoryListView
}
