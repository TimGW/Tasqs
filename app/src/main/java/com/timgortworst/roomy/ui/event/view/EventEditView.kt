package com.timgortworst.roomy.ui.event.view

import com.timgortworst.roomy.model.EventCategory
import com.timgortworst.roomy.model.User

interface EventEditView {
    fun presentUserList(users: MutableList<User>)
    fun presentCategoryList(tasks: MutableList<EventCategory>)
    fun presentFormattedDate(formattedDayOfMonth: String, formattedMonth: String?, formattedYear: String)
}