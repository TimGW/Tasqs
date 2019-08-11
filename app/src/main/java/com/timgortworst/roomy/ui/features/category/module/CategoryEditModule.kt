package com.timgortworst.roomy.ui.features.category.module

import com.timgortworst.roomy.ui.features.category.view.CategoryEditActivity
import com.timgortworst.roomy.ui.features.category.view.CategoryEditView
import dagger.Binds
import dagger.Module

@Module
abstract class CategoryEditModule {
    @Binds
    internal abstract fun provideCategoryEditView(editTaskActivity: CategoryEditActivity): CategoryEditView
}
