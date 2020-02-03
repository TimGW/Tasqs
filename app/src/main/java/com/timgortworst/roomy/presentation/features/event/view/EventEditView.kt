package com.timgortworst.roomy.presentation.features.event.view

import com.timgortworst.roomy.data.model.User

interface EventEditView {
    fun presentUserList(users: MutableList<User>)
    fun presentFormattedDate(formattedDayOfMonth: String, formattedMonth: String?, formattedYear: String)
}