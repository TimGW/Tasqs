package com.timgortworst.roomy.domain.usecase

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.domain.model.Response

class LoginUseCase(
    private val householdRepository: HouseholdRepository,
    private val userRepository: UserRepository
) {
    suspend fun handleLoginResult(
        fbUser: FirebaseUser?,
        newUser: Boolean,
        registrationToken: String
    ): Response<String> {
        val fireBaseUser = fbUser ?: return Response.Error()

        return try {
            if (newUser) {
                val householdId = householdRepository.createHousehold()
                userRepository.createUser(householdId, fireBaseUser, registrationToken)
            } else {
                userRepository.addUserToken(fireBaseUser.uid, registrationToken)
            }
            Response.Success(fbUser.displayName.orEmpty())
        } catch (e: FirebaseFirestoreException) {
            Response.Error(e)
        }
    }
}
