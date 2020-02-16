package com.timgortworst.roomy.presentation.features.event.view

import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import com.timgortworst.roomy.data.model.EventMetaData
import com.timgortworst.roomy.data.model.User

interface EventEditView {
    fun presentUserList(users: MutableList<User>)
    fun presentFormattedDate(formattedDayOfMonth: String, formattedMonth: String?, formattedYear: String)
    fun presentEmptyDescriptionError(@StringRes errorMessage: Int)
    fun finishActivity()
    fun inflatePopUpMenu(@MenuRes menuId: Int)
    fun updateRecurrenceButtonText(selectedRecurrenceType: Int)
}