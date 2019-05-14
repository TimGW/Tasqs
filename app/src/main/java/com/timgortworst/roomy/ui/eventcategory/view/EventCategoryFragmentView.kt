package com.timgortworst.roomy.ui.eventcategory.view

import com.timgortworst.roomy.model.EventCategory

interface EventCategoryFragmentView {
    fun presentNewCategory(householdTask: EventCategory)
    fun presentEditedCategory(householdTask: EventCategory)
    fun presentDeletedCategory(householdTask: EventCategory)
}
