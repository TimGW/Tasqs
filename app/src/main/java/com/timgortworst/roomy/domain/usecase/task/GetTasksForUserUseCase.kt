package com.timgortworst.roomy.domain.usecase.task

import com.google.firebase.firestore.Query
import com.timgortworst.roomy.domain.repository.TaskRepository
import com.timgortworst.roomy.domain.repository.UserRepository
import com.timgortworst.roomy.domain.usecase.SuspendUseCase

class GetTasksForUserUseCase(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository
) : SuspendUseCase<Query> {

    override suspend fun invoke()= taskRepository.getTasksForUserQuery(
        userRepository.getFbUser()?.uid.orEmpty()
    )
}

