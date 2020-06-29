package com.timgortworst.tasqs.presentation.usecase.user

import com.timgortworst.tasqs.domain.usecase.UseCase
import com.timgortworst.tasqs.domain.usecase.user.RemoveUserUseCaseImpl

interface RemoveUserUseCase : UseCase<String, RemoveUserUseCaseImpl.Params>