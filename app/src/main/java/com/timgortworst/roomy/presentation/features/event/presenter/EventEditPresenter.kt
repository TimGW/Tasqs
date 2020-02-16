package com.timgortworst.roomy.presentation.features.event.presenter

import android.text.Editable
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.textfield.TextInputEditText
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.model.User
import com.timgortworst.roomy.domain.usecase.EventUseCase
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

    fun editEventDone(selectedDate: LocalDate,
                      eventId: String?,
                      user: User,
                      eventDescription: String,
                      recurrenceFrequency: String,
                      recurrenceTypeId: Int,
                      selectedWeekDays: List<Int>)= scope.launch {
        if (eventDescription.isEmpty()) {
            view.presentEmptyDescriptionError(R.string.event_edit_error_empty_description)
            return@launch
        }

        eventUseCase.createOrUpdateEvent(eventId, eventDescription, user, selectedDate,
                recurrenceFrequency, recurrenceTypeId, selectedWeekDays)

        view.finishActivity()
    }

    fun checkForPluralUI(numberInput: String) {
        when {
            numberInput.toIntOrNull()?.equals(1) == true || numberInput.isBlank() ->
                view.inflatePopUpMenu(R.menu.recurrence_popup_menu)
            else -> view.inflatePopUpMenu(R.menu.recurrence_popup_menu_plural)
        }
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

    companion object {
        private const val TAG = "EventEditPresenter"
    }
}
