package com.timgortworst.tasqs.domain.usecase.task

import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.tasqs.domain.repository.TaskRepository
import com.timgortworst.tasqs.domain.usecase.None
import com.timgortworst.tasqs.presentation.usecase.task.GetTasksForUserQueryUseCase

class GetTasksForUserQueryUseCaseImpl(
    private val taskRepository: TaskRepository
) : GetTasksForUserQueryUseCase {

    override suspend fun execute(params: None) = taskRepository.getTasksForUserQuery(
        FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    )
}

