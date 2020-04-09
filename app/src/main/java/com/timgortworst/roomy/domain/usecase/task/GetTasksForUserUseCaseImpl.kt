package com.timgortworst.roomy.domain.usecase.task

import com.timgortworst.roomy.domain.repository.TaskRepository
import com.timgortworst.roomy.domain.repository.UserRepository
import com.timgortworst.roomy.presentation.usecase.GetTasksForUserUseCase

class GetTasksForUserUseCaseImpl(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository
) : GetTasksForUserUseCase {

    override suspend fun execute(params: Unit?)= taskRepository.getTasksForUserQuery(
        userRepository.getFbUser()?.uid.orEmpty()
    )
}

