package com.timgortworst.roomy.presentation.features.event.view

import androidx.annotation.StringRes
import com.timgortworst.roomy.data.model.User

interface EventEditView {
    fun presentUserList(users: MutableList<User>)
    fun presentFormattedDate(formattedDayOfMonth: String, formattedMonth: String?, formattedYear: String)
    fun presentEmptyDescriptionError(@StringRes errorMessage: Int)
    fun finishActivity()
}