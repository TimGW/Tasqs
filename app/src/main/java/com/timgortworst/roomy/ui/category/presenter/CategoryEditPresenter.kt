package com.timgortworst.roomy.ui.category.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.timgortworst.roomy.repository.AgendaRepository
import com.timgortworst.roomy.ui.category.view.CategoryEditView
import com.timgortworst.roomy.utils.CoroutineLifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CategoryEditPresenter(
        val view: CategoryEditView,
        val repository: AgendaRepository
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
            repository.updateCategory(eventCategoryId, name, description)
        } else {
            repository.insertCategory(name, description)
        }
    }

    companion object {
        private const val TAG = "EditTaskPresenter"
    }
}
