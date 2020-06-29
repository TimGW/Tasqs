package com.timgortworst.tasqs.presentation.features.task.adapter

import com.timgortworst.tasqs.domain.model.Task

interface TaskClickListener {
    fun onTaskInfoClicked(task: Task)
}