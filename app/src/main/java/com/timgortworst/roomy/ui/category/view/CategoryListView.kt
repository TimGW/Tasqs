package com.timgortworst.roomy.ui.category.view

import com.timgortworst.roomy.model.Category
import com.timgortworst.roomy.ui.main.view.PageStateListener

interface CategoryListView : PageStateListener {
    fun presentAddedCategory(category: Category)
    fun presentEditedCategory(category: Category)
    fun presentDeletedCategory(category: Category)
    fun presentEmptyView(isVisible: Boolean)
}
