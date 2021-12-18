package com.timgortworst.tasqs.presentation.usecase.user

import com.timgortworst.tasqs.domain.usecase.FlowUseCase
import com.timgortworst.tasqs.domain.usecase.user.RemoveUserUseCaseImpl

interface RemoveUserUseCase : FlowUseCase<String, RemoveUserUseCaseImpl.Params>