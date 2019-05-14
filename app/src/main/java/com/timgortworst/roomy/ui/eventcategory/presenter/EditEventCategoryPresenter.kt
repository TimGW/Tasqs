package com.timgortworst.roomy.ui.eventcategory.presenter

import com.timgortworst.roomy.repository.AgendaRepository
import com.timgortworst.roomy.ui.eventcategory.view.EditEventCategoryView
import kotlinx.coroutines.InternalCoroutinesApi


class EditEventCategoryPresenter(
    val view: EditEventCategoryView,
    val repository: AgendaRepository
) {

    fun insertOrUpdateCategory(
        eventCategoryId: String,
        name: String,
        description: String,
        points: Int) {

        if(eventCategoryId.isNotBlank()) {
            repository.updateCategory(eventCategoryId, name, description, points)
        } else {
            repository.insertCategory(name, description, points)
        }
    }

    companion object {
        private const val TAG = "EditTaskPresenter"
    }
}
