package com.timgortworst.roomy.ui.eventcategory.presenter

import com.timgortworst.roomy.model.EventCategory
import com.timgortworst.roomy.repository.AgendaRepository
import com.timgortworst.roomy.ui.eventcategory.view.EventCategoryFragmentView
import kotlinx.coroutines.InternalCoroutinesApi


class EventCategoryPresenter(
    val view: EventCategoryFragmentView,
    val repository: AgendaRepository
) : AgendaRepository.EventCategoryListener {

    fun listenToTasks() {
        repository.listenToCategories(this)
    }

    fun detachTaskListener(){
        repository.detachTaskListener()
    }

    fun deleteEventCategory(agendaEventCategory: EventCategory) {
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
