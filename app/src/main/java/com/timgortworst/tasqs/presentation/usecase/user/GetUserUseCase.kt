package com.timgortworst.tasqs.presentation.usecase.user

import com.timgortworst.tasqs.domain.model.User
import com.timgortworst.tasqs.domain.usecase.UseCase
import com.timgortworst.tasqs.domain.usecase.user.GetUserUseCaseImpl

interface GetUserUseCase : UseCase<User, GetUserUseCaseImpl.Params>