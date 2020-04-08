package com.timgortworst.roomy.domain.usecase.user

import com.google.firebase.auth.FirebaseUser
import com.timgortworst.roomy.domain.repository.UserRepository
import com.timgortworst.roomy.domain.usecase.UseCase

class GetFbUserUseCase(
    private val userRepository: UserRepository
) : UseCase<FirebaseUser?, Unit> {

    override fun execute(params: Unit?) = userRepository.getFbUser()
}
