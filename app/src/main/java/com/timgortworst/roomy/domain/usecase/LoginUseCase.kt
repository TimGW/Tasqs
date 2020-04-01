package com.timgortworst.roomy.domain.usecase

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.domain.ErrorHandler
import com.timgortworst.roomy.domain.model.response.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class LoginUseCase(
    private val householdRepository: HouseholdRepository,
    private val userRepository: UserRepository,
    private val errorHandler: ErrorHandler
) {
    fun handleLoginResult(
        fbUser: FirebaseUser?,
        newUser: Boolean,
        registrationToken: String
    ): Flow<Response<String>> = flow {
        emit(Response.Loading)
        val fireBaseUser = fbUser ?: run { emit(Response.Error()); return@flow }

        try {
            if (newUser) {
                val householdId = householdRepository.createHousehold()
                userRepository.createUser(householdId, fireBaseUser, registrationToken)
            } else {
                userRepository.addUserToken(fireBaseUser.uid, registrationToken)
            }
            Response.Success(fbUser.displayName.orEmpty())
        } catch (e: FirebaseFirestoreException) {
            Response.Error(errorHandler.getError(e))
        }
    }.flowOn(Dispatchers.IO)
}
