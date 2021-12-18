package com.timgortworst.tasqs.presentation.usecase.user

import com.timgortworst.tasqs.domain.model.User
import com.timgortworst.tasqs.domain.usecase.None
import com.timgortworst.tasqs.domain.usecase.FlowUseCase

interface GetAllUsersUseCase : FlowUseCase<List<User>, None>