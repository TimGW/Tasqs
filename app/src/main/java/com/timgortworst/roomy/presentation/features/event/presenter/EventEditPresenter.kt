package com.timgortworst.roomy.presentation.features.event.presenter

import android.text.Editable
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.textfield.TextInputEditText
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
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.TextStyle
import org.threeten.bp.temporal.ChronoField
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
                      selectedDate: LocalDate,
                      eventId: String?,
                      user: User,
                      eventDescription: String,
                      repeatInterval: EventMetaData.EventInterval)= scope.launch {
        if (eventDescription.isEmpty()) {
            view.presentEmptyDescriptionError(R.string.event_edit_error_empty_description)
            return@launch
        }

        val eventMetaData = if (isRepeatBoxChecked) {
            EventMetaData(
                    eventTimestamp = selectedDate.toZonedDateTime(),
                    eventInterval = repeatInterval
            )
        } else {
            EventMetaData(
                    eventTimestamp = selectedDate.toZonedDateTime(),
                    eventInterval = EventMetaData.EventInterval.SingleEvent)
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

    fun checkForPluralUI(numberInput: String, currentSelectedMenuItemId: Int) {
        when {
            numberInput.toIntOrNull()?.equals(1) == true || numberInput.isBlank() ->
                view.inflatePopUpMenu(R.menu.recurrence_popup_menu)
            else -> view.inflatePopUpMenu(R.menu.recurrence_popup_menu_plural)
        }
        view.updateRecurrenceButtonText(currentSelectedMenuItemId)
    }

    fun disableInputZero(editable: Editable?) {
        editable?.let {
            val input = it.toString()
            if (input.isNotEmpty() && input.first() == '0') {
                it.replace(0, 1, "1")
            }
        }
    }

    fun disableEmptyInput(view: TextInputEditText?, hasFocus: Boolean) {
        view?.apply {
            if (text.toString().isBlank() && !hasFocus) {
                setText("1")
            }
        }
    }

    fun setSelectedRecurrence(itemId: Int,
                              recurrenceInterval: String = "1",
                              selectedWeekDays: List<Int> = listOf(ZonedDateTime.now().get(ChronoField.DAY_OF_WEEK) - 1),
                              monthRepeat: EventMetaData.EventInterval.MonthRepeat = EventMetaData.EventInterval.MonthRepeat.DAY_OF_MONTH
                              ) {
        val interval = recurrenceInterval.toIntOrNull() ?: 1
        val recurrence = when(itemId) {
            R.id.days -> EventMetaData.EventInterval.Daily(interval)
            R.id.weeks -> EventMetaData.EventInterval.Weekly(interval, selectedWeekDays)
            R.id.months -> EventMetaData.EventInterval.Monthly(interval, monthRepeat)
            R.id.year -> EventMetaData.EventInterval.Annually(interval)
            else -> EventMetaData.EventInterval.SingleEvent
       }
        view.presentRecurrenceInterval(recurrence)
    }

    companion object {
        private const val TAG = "EventEditPresenter"
    }
}
