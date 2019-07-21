package com.timgortworst.roomy.ui.category.view

import com.timgortworst.roomy.model.Category

interface CategoryListView {
    fun presentNewCategory(householdTask: Category)
    fun presentEditedCategory(householdTask: Category)
    fun presentDeletedCategory(householdTask: Category)
    fun showLoadingState(isLoading: Boolean)
}
