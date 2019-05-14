package com.timgortworst.roomy.ui.agenda.ui

import com.timgortworst.roomy.model.EventCategory
import com.timgortworst.roomy.model.User

interface EditAgendaEventView {
    fun presentUserList(users: MutableList<User>)
    fun presentCategoryList(tasks: MutableList<EventCategory>)
    fun presentFormattedDate(formattedDayOfMonth: String, formattedMonth: String?, formattedYear: String)
}