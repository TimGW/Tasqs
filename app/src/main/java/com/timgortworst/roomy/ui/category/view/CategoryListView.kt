package com.timgortworst.roomy.ui.category.view

import com.timgortworst.roomy.model.Category

interface CategoryListView {
    fun presentAddedCategory(category: Category)
    fun presentEditedCategory(category: Category)
    fun presentDeletedCategory(category: Category)
    fun presentErrorView()
    fun presentEmptyView(isVisible: Boolean)
    fun setLoadingView(isLoading: Boolean)
}
