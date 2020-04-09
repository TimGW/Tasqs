package com.timgortworst.roomy.presentation.usecase

import androidx.lifecycle.LiveData
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.usecase.UseCase

interface GetAllUsersUseCase : UseCase<LiveData<Response<List<User>>>, Unit>