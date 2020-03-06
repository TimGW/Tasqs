package com.timgortworst.roomy.presentation.base.view

import com.firebase.ui.common.ChangeEventType
import com.timgortworst.roomy.domain.model.Task

interface ChildChangedListener {
    fun onChildChanged(
        type: ChangeEventType,
        task: Task?
    )
}