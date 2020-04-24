package com.timgortworst.roomy.domain.usecase.task

import com.timgortworst.roomy.domain.repository.TaskRepository
import com.timgortworst.roomy.presentation.usecase.task.GetAllTasksUseCase

class GetAllTasksUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetAllTasksUseCase {

    override suspend fun execute(params: Unit?) = taskRepository.getAllTasksQuery()
}

