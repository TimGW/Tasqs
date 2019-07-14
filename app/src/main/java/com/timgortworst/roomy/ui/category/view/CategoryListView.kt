package com.timgortworst.roomy.ui.category.view

import com.timgortworst.roomy.model.EventCategory

interface CategoryListView {
    fun presentNewCategory(householdTask: EventCategory)
    fun presentEditedCategory(householdTask: EventCategory)
    fun presentDeletedCategory(householdTask: EventCategory)
}
