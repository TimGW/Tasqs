package com.timgortworst.roomy.presentation.features.event.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.timgortworst.roomy.data.model.Category
import com.timgortworst.roomy.data.model.EventMetaData
import com.timgortworst.roomy.data.model.User
import com.timgortworst.roomy.domain.usecase.EventUseCase
import com.timgortworst.roomy.presentation.base.CoroutineLifecycleScope
import com.timgortworst.roomy.presentation.features.event.view.EventEditView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


class EventEditPresenter @Inject constructor(
        private val view: EventEditView,
        private val eventUseCase: EventUseCase
) : DefaultLifecycleObserver {

    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun createOrUpdateEvent(
            eventId: String?,
            category: Category,
            user: User,
            eventMetaData: EventMetaData
    ) = scope.launch {
        if (!eventId.isNullOrEmpty()) {
            eventUseCase.updateEvent(eventId, eventMetaData, category, user)
        } else {
            val householdId = eventUseCase.getHouseholdIdForUser()
            eventUseCase.createEvent(eventMetaData, category, user, householdId)
        }
    }

    fun getUsers() = scope.launch {
        val userList = eventUseCase.getUserListForCurrentHousehold()
        view.presentUserList(userList?.toMutableList() ?: mutableListOf())
    }

    fun getCategories() = scope.launch {
        val categories = eventUseCase.getCategories()
        view.presentCategoryList(categories.toMutableList())
    }

    fun formatDateAndSetUI(cal: Calendar) {
        val formattedDayOfMonth = cal.get(Calendar.DAY_OF_MONTH).toString()
        val formattedMonth = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        val formattedYear = cal.get(Calendar.YEAR).toString()
        view.presentFormattedDate(formattedDayOfMonth, formattedMonth, formattedYear)
    }

    companion object {
        private const val TAG = "EventEditPresenter"
    }
}
