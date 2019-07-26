package com.timgortworst.roomy.ui.category.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.timgortworst.roomy.repository.CategoryRepository
import com.timgortworst.roomy.ui.category.view.CategoryEditView
import com.timgortworst.roomy.utils.CoroutineLifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class CategoryEditPresenter @Inject constructor(
    val view: CategoryEditView,
    private val categoryRepository: CategoryRepository
) : DefaultLifecycleObserver {

    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun insertOrUpdateCategory(
        eventCategoryId: String,
        name: String,
        description: String) = scope.launch {

        if (eventCategoryId.isNotBlank()) {
            categoryRepository.updateCategory(eventCategoryId, name, description)
        } else {
            categoryRepository.createCategory(name, description)
        }
    }

    companion object {
        private const val TAG = "CategoryEditPresenter"
    }
}
