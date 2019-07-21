package com.timgortworst.roomy.ui.category.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.timgortworst.roomy.model.Category
import com.timgortworst.roomy.repository.EventRepository
import com.timgortworst.roomy.ui.category.view.CategoryListView
import com.timgortworst.roomy.utils.CoroutineLifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CategoryListPresenter(
        val view: CategoryListView,
        val repository: EventRepository
) : EventRepository.EventCategoryListener, DefaultLifecycleObserver {

    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun listenToTasks() = scope.launch {
        repository.listenToCategories(this@CategoryListPresenter)
    }

    fun detachTaskListener() {
        repository.detachTaskListener()
    }

    fun deleteEventCategory(agendaEventCategory: Category) = scope.launch {
        repository.deleteCategoryForHousehold(agendaEventCategory)
    }

    override fun categoryAdded(agendaEventCategory: Category) {
        view.presentNewCategory(agendaEventCategory)
    }

    override fun categoryModified(agendaEventCategory: Category) {
        view.presentEditedCategory(agendaEventCategory)
    }

    override fun categoryDeleted(agendaEventCategory: Category) {
        view.presentDeletedCategory(agendaEventCategory)
    }
}
