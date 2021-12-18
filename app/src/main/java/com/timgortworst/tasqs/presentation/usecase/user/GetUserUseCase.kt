package com.timgortworst.tasqs.presentation.usecase.user

import com.timgortworst.tasqs.domain.model.User
import com.timgortworst.tasqs.domain.usecase.FlowUseCase
import com.timgortworst.tasqs.domain.usecase.user.GetUserUseCaseImpl

interface GetUserUseCase : FlowUseCase<User, GetUserUseCaseImpl.Params>