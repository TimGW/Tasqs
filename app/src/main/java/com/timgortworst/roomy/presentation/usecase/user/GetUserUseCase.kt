package com.timgortworst.roomy.presentation.usecase.user

import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.user.GetUserUseCaseImpl

interface GetUserUseCase : UseCase<User, GetUserUseCaseImpl.Params>