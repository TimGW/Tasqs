package com.timgortworst.roomy.ui.features.category.view

import com.timgortworst.roomy.data.model.Category
import com.timgortworst.roomy.ui.base.view.PageStateListener

interface CategoryListView : PageStateListener {
    fun presentAddedCategory(category: Category)
    fun presentEditedCategory(category: Category)
    fun presentDeletedCategory(category: Category)
    fun presentEmptyView(isVisible: Boolean)
}
