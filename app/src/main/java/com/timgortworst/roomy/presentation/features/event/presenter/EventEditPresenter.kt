package com.timgortworst.roomy.presentation.features.event.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.timgortworst.roomy.data.model.Category
import com.timgortworst.roomy.data.model.Event
import com.timgortworst.roomy.data.model.EventMetaData
import com.timgortworst.roomy.data.model.User
import com.timgortworst.roomy.domain.usecase.EventUseCase
import com.timgortworst.roomy.domain.utils.toTimestamp
import com.timgortworst.roomy.presentation.base.CoroutineLifecycleScope
import com.timgortworst.roomy.presentation.features.event.view.EventEditView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.format.TextStyle
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

    fun formatDateAndSetUI(localDateTime: LocalDate) {
        val formattedDayOfMonth = localDateTime.dayOfMonth.toString()
        val formattedMonth = localDateTime.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        val formattedYear = localDateTime.year.toString()
        view.presentFormattedDate(formattedDayOfMonth, formattedMonth, formattedYear)
    }

    fun editEventDone(isRepeatBoxChecked: Boolean,
                      repeatInterval: EventMetaData.EventInterval,
                      selectedDate: LocalDate,
                      event: Event?,
                      category: Category,
                      user: User) {

        val eventMetaData = if (isRepeatBoxChecked) {
            EventMetaData(eventTimestamp = selectedDate.toTimestamp(), eventInterval = repeatInterval)
        } else {
            EventMetaData(eventTimestamp = selectedDate.toTimestamp(), eventInterval = EventMetaData.EventInterval.SINGLE_EVENT)
        }

        createOrUpdateEvent(
                event?.eventId,
                category,
                user,
                eventMetaData
        )
    }

    companion object {
        private const val TAG = "EventEditPresenter"
    }
}
