package com.timgortworst.roomy.domain.usecase.user

import com.timgortworst.roomy.domain.repository.UserRepository
import com.timgortworst.roomy.domain.usecase.SuspendUseCase
import com.timgortworst.roomy.presentation.usecase.AddTokenUseCase

class AddTokenUseCaseImpl(
    private val userRepository: UserRepository
) : AddTokenUseCase {

    data class Params(val token: String)

    override suspend fun execute(params: Params?) {
        checkNotNull(params)

        userRepository.addUserToken(userRepository.getFbUser()?.uid, params.token)
    }
}
