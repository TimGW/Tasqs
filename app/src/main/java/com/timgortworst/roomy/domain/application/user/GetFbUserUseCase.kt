package com.timgortworst.roomy.domain.application.user

import com.google.firebase.auth.FirebaseUser
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.domain.application.UseCase

class GetFbUserUseCase(
    private val userRepository: UserRepository
) : UseCase<FirebaseUser?> {

    override fun invoke()= userRepository.getFbUser()
}
