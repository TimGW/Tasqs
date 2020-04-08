package com.timgortworst.roomy.domain.usecase.user

import com.timgortworst.roomy.domain.repository.UserRepository
import com.timgortworst.roomy.domain.usecase.SuspendUseCase

class AddTokenUseCase(
    private val userRepository: UserRepository
) : SuspendUseCase<Unit, AddTokenUseCase.Params> {

    data class Params(val token: String)

    override suspend fun execute(params: Params?) {
        checkNotNull(params)

        userRepository.addUserToken(userRepository.getFbUser()?.uid, params.token)
    }
}
