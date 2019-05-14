package com.timgortworst.roomy.ui.agenda.presenter

import com.timgortworst.roomy.model.EventCategory
import com.timgortworst.roomy.model.EventMetaData
import com.timgortworst.roomy.model.User
import com.timgortworst.roomy.repository.AgendaRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.agenda.ui.EditAgendaEventView
import kotlinx.coroutines.InternalCoroutinesApi
import java.util.*


class EditAgendaEventPresenter(
    val view: EditAgendaEventView,
    val agendaRepository: AgendaRepository,
    val userRepository: UserRepository) {

    fun insertOrUpdateEvent(
        eventId: String,
        category: EventCategory,
        user: User,
        eventMetaData: EventMetaData,
        isDone : Boolean = false) {

        if (eventId.isNotBlank()) {
            agendaRepository.updateAgendaEvent(eventId, category, user, eventMetaData, isDone)
        } else {
            agendaRepository.insertAgendaEvent(category, user, eventMetaData, isDone)
        }
    }

    fun fetchUsers() {
        userRepository.getUsersForHousehold(object : UserRepository.UserListener {
            override fun provideUserList(users: MutableList<User>) {
                view.presentUserList(users)
            }
        })
    }

    fun fetchEventCategories() {
        agendaRepository.getCategories { categories -> view.presentCategoryList(categories) }
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
