package com.timgortworst.roomy.presentation.features.task.adapter

import com.timgortworst.roomy.domain.model.Task

interface TaskClickListener {
    fun onTaskInfoClicked(task: Task)
}