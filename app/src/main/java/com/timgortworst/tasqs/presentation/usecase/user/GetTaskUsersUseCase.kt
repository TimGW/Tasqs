package com.timgortworst.tasqs.presentation.usecase.user

import com.timgortworst.tasqs.domain.model.Task
import com.timgortworst.tasqs.domain.usecase.None
import com.timgortworst.tasqs.domain.usecase.UseCase

interface GetTaskUsersUseCase : UseCase<List<Task.User>, None>