package com.timgortworst.roomy.ui.agenda.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import com.timgortworst.roomy.model.EventCategory
import com.timgortworst.roomy.model.EventMetaData
import com.timgortworst.roomy.model.User
import com.timgortworst.roomy.repository.AgendaRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.agenda.ui.EditAgendaEventView
import com.timgortworst.roomy.utils.CoroutineLifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class EditAgendaEventPresenter(
    private val view: EditAgendaEventView,
    private val agendaRepository: AgendaRepository,
    private val userRepository: UserRepository,
    private val sharedPref: HuishoudGenootSharedPref
) : DefaultLifecycleObserver {

    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun insertOrUpdateEvent(
        eventId: String,
        category: EventCategory,
        user: User,
        eventMetaData: EventMetaData,
        isDone: Boolean = false
    ) = scope.launch {

        if (eventId.isNotBlank()) {
            agendaRepository.updateAgendaEvent(eventId, category, user, eventMetaData, isDone)
        } else {
            agendaRepository.insertAgendaEvent(category, user, eventMetaData, isDone)
        }
    }

    fun fetchUsers() = scope.launch {
        val userList = userRepository.getUsersForHouseholdId(sharedPref.getActiveHouseholdId())
        view.presentUserList(userList.toMutableList())
    }

    fun fetchEventCategories() = scope.launch {
        val categories = agendaRepository.getCategories()
        view.presentCategoryList(categories.toMutableList())
    }

    fun formatDate(year: Int, month: Int, dayOfMonth: Int) {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        val formattedDayOfMonth = cal.get(Calendar.DAY_OF_MONTH).toString()
        val formattedMonth = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        val formattedYear = cal.get(Calendar.YEAR).toString()

        view.presentFormattedDate(formattedDayOfMonth, formattedMonth, formattedYear)
    }


    companion object {
        private const val TAG = "EditAgendaEventPresenter"
    }
}
