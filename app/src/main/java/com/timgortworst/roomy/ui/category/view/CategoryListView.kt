package com.timgortworst.roomy.ui.category.view

import com.timgortworst.roomy.model.Category
import com.timgortworst.roomy.repository.DataLoadingListener

interface CategoryListView : DataLoadingListener {
    fun presentNewCategory(householdTask: Category)
    fun presentEditedCategory(householdTask: Category)
    fun presentDeletedCategory(householdTask: Category)
}
