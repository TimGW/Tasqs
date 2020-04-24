package com.timgortworst.roomy.presentation.usecase.user

import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.usecase.UseCase
import kotlinx.coroutines.flow.Flow

interface GetAllUsersUseCase : UseCase<List<User>, Unit>