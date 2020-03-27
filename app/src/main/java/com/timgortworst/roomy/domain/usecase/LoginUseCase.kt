package com.timgortworst.roomy.domain.usecase

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.domain.model.ResponseState

class LoginUseCase(
    private val householdRepository: HouseholdRepository,
    private val userRepository: UserRepository
) {
    suspend fun handleLoginResult(
        fbUser: FirebaseUser?,
        newUser: Boolean,
        registrationToken: String
    ): ResponseState {
        val fireBaseUser = fbUser ?: return ResponseState.Error(R.string.error_generic)

        return try {
            if (newUser) {
                val householdId = householdRepository.createHousehold()
                userRepository.createUser(householdId, fireBaseUser, registrationToken)
            } else {
                userRepository.addUserToken(fireBaseUser.uid, registrationToken)
            }
            ResponseState.Success(fbUser.displayName)
        } catch (e: FirebaseFirestoreException) {
            ResponseState.Error(R.string.error_generic)
        }
    }
}
