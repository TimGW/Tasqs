package com.timgortworst.roomy.ui.category.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.timgortworst.roomy.repository.CategoryRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.category.view.CategoryEditView
import com.timgortworst.roomy.utils.CoroutineLifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


class CategoryEditPresenter @Inject constructor(
        val view: CategoryEditView,
        private val categoryRepository: CategoryRepository,
        private val userRepository: UserRepository
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
            categoryRepository.updateCategory(categoryId, name, description)
        } else {
            categoryRepository.createCategory(name, description, userRepository.getHouseholdIdForUser())
        }
    }

    companion object {
        private const val TAG = "CategoryEditPresenter"
    }
}
