package com.timgortworst.roomy.ui.eventcategory.presenter

import android.arch.lifecycle.DefaultLifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import com.timgortworst.roomy.model.EventCategory
import com.timgortworst.roomy.repository.AgendaRepository
import com.timgortworst.roomy.ui.eventcategory.view.EventCategoryFragmentView
import com.timgortworst.roomy.utils.CoroutineLifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class EventCategoryPresenter(
    val view: EventCategoryFragmentView,
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
