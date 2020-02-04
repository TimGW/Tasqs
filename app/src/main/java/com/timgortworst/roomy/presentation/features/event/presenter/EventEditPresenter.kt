package com.timgortworst.roomy.presentation.features.event.presenter

import android.text.Editable
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.model.EventMetaData
import com.timgortworst.roomy.data.model.User
import com.timgortworst.roomy.data.utils.Constants.DEFAULT_HOUR_OF_DAY_NOTIFICATION
import com.timgortworst.roomy.domain.usecase.EventUseCase
import com.timgortworst.roomy.presentation.base.CoroutineLifecycleScope
import com.timgortworst.roomy.presentation.features.event.view.EventEditView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
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

    fun getUsers() = scope.launch {
        val userList = eventUseCase.getUserListForCurrentHousehold()
        view.presentUserList(userList?.toMutableList() ?: mutableListOf())
    }

    fun formatDate(localDateTime: LocalDate) {
        val formattedDayOfMonth = localDateTime.dayOfMonth.toString()
        val formattedMonth = localDateTime.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        val formattedYear = localDateTime.year.toString()
        view.presentFormattedDate(formattedDayOfMonth, formattedMonth, formattedYear)
    }

    fun editEventDone(isRepeatBoxChecked: Boolean,
                      repeatInterval: EventMetaData.EventInterval?,
                      selectedDate: LocalDate,
                      eventId: String?,
                      user: User,
                      eventDescription: String)= scope.launch {
        if (eventDescription.isEmpty()) {
            view.presentEmptyDescriptionError(R.string.event_edit_error_empty_description)
            return@launch
        }

        val eventMetaData = if (isRepeatBoxChecked && repeatInterval != null) {
            EventMetaData(
                    eventTimestamp = selectedDate.toZonedDateTime(),
                    eventInterval = repeatInterval
            )
        } else {
            EventMetaData(
                    eventTimestamp = selectedDate.toZonedDateTime(),
                    eventInterval = EventMetaData.EventInterval.SINGLE_EVENT)
        }

        if (!eventId.isNullOrEmpty()) {
            eventUseCase.updateEvent(eventId, eventMetaData, user, eventDescription)
        } else {
            val householdId = eventUseCase.getHouseholdIdForUser()
            eventUseCase.createEvent(eventMetaData, user, householdId, eventDescription)
        }

        view.finishActivity()
    }

    private fun LocalDate.toZonedDateTime(timeOfDay: Int = DEFAULT_HOUR_OF_DAY_NOTIFICATION) = atTime(timeOfDay, 0).atZone(ZoneId.systemDefault())

    companion object {
        private const val TAG = "EventEditPresenter"
    }
}
