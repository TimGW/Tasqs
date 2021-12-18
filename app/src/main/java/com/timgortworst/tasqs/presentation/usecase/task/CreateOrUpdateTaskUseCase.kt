package com.timgortworst.tasqs.presentation.usecase.task

import com.timgortworst.tasqs.domain.model.Task
import com.timgortworst.tasqs.domain.usecase.FlowUseCase
import com.timgortworst.tasqs.domain.usecase.task.CreateOrUpdateTaskUseCaseImpl

interface CreateOrUpdateTaskUseCase :
    FlowUseCase<Task, CreateOrUpdateTaskUseCaseImpl.Params>