package com.timgortworst.roomy.ui.category.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.timgortworst.roomy.model.EventCategory
import com.timgortworst.roomy.repository.AgendaRepository
import com.timgortworst.roomy.ui.category.view.CategoryListView
import com.timgortworst.roomy.utils.CoroutineLifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CategoryListPresenter(
        val view: CategoryListView,
        val repository: AgendaRepository
) : AgendaRepository.EventCategoryListener, DefaultLifecycleObserver {

    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun listenToTasks() {
        repository.listenToCategories(this)
    }

    fun detachTaskListener() {
        repository.detachTaskListener()
    }

    fun deleteEventCategory(agendaEventCategory: EventCategory) = scope.launch {
        repository.deleteEventCategoryForHousehold(agendaEventCategory)
    }

    override fun categoryAdded(agendaEventCategory: EventCategory) {
        view.presentNewCategory(agendaEventCategory)
    }

    override fun categoryModified(agendaEventCategory: EventCategory) {
        view.presentEditedCategory(agendaEventCategory)
    }

    override fun categoryDeleted(agendaEventCategory: EventCategory) {
        view.presentDeletedCategory(agendaEventCategory)
    }
}
