package com.timgortworst.roomy.domain.usecase.user

import com.timgortworst.roomy.domain.repository.UserRepository
import com.timgortworst.roomy.presentation.usecase.GetFbUserUseCase

class GetFbUserUseCaseImpl(
    private val userRepository: UserRepository
) : GetFbUserUseCase {

    override fun execute(params: Unit?) = userRepository.getFbUser()
}
