package com.timgortworst.roomy.ui.category.view

import com.timgortworst.roomy.model.Category
import com.timgortworst.roomy.repository.ObjectStateListener

interface CategoryListView : ObjectStateListener {
    fun presentNewCategory(householdTask: Category)
    fun presentEditedCategory(householdTask: Category)
    fun presentDeletedCategory(householdTask: Category)
}
