package com.timgortworst.roomy.ui.features.category.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.timgortworst.roomy.domain.usecase.CategoryUseCase
import com.timgortworst.roomy.ui.base.CoroutineLifecycleScope
import com.timgortworst.roomy.ui.features.category.view.CategoryEditView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class CategoryEditPresenter @Inject constructor(
        view: CategoryEditView,
        private val categoryUseCase: CategoryUseCase
) : DefaultLifecycleObserver {

    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun insertOrUpdateCategory(
            categoryId: String?,
            name: String,
            description: String) = scope.launch {

        if (!categoryId.isNullOrEmpty()) {
            categoryUseCase.updateCategory(categoryId, name, description)
        } else {
            categoryUseCase.createCategory(name, description)
        }
    }

    companion object {
        private const val TAG = "CategoryEditPresenter"
    }
}
