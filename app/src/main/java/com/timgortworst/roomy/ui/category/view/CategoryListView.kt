package com.timgortworst.roomy.ui.category.view

import com.timgortworst.roomy.R
import com.timgortworst.roomy.model.Category

interface CategoryListView {
    fun presentAddedCategory(category: Category)
    fun presentEditedCategory(category: Category)
    fun presentDeletedCategory(category: Category)
    fun presentEmptyView(isVisible: Boolean)
    fun setLoadingView(isLoading: Boolean)
    fun setErrorView(isVisible: Boolean,
                     title: Int = R.string.error_list_state_title,
                     message: Int = R.string.error_list_state_text)
}
