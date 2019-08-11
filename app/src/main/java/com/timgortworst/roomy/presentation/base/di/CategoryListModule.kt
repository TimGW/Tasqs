package com.timgortworst.roomy.presentation.base.di

import com.timgortworst.roomy.presentation.features.category.view.CategoryListFragment
import com.timgortworst.roomy.presentation.features.category.view.CategoryListView
import dagger.Binds
import dagger.Module

@Module
abstract class CategoryListModule {
    @Binds
    internal abstract fun provideCategoryListView(mainTasksFragment: CategoryListFragment): CategoryListView
}
