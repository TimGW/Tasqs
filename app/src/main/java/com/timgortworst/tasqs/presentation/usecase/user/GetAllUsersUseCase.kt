package com.timgortworst.tasqs.presentation.usecase.user

import com.timgortworst.tasqs.domain.model.User
import com.timgortworst.tasqs.domain.usecase.None
import com.timgortworst.tasqs.domain.usecase.UseCase

interface GetAllUsersUseCase : UseCase<List<User>, None>