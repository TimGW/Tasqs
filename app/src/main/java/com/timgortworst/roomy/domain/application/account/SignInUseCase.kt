package com.timgortworst.roomy.domain.application.account

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.domain.repository.HouseholdRepository
import com.timgortworst.roomy.domain.repository.UserRepository
import com.timgortworst.roomy.domain.application.UseCase
import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.model.response.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


class SignInUseCase(
    private val householdRepository: HouseholdRepository,
    private val userRepository: UserRepository,
    private val errorHandler: ErrorHandler
) : UseCase<Flow<Response<String>>> {
    private var registrationToken: String? = null
    private var fbUser: FirebaseUser? = null
    private var newUser: Boolean? = null

    fun init(fbUser: FirebaseUser?, newUser: Boolean, registrationToken: String): SignInUseCase {
        this.fbUser = fbUser
        this.newUser = newUser
        this.registrationToken = registrationToken
        return this
    }

    override fun invoke() = flow {
        if (fbUser == null || registrationToken == null || newUser == null) {
            throw IllegalArgumentException("init not called, or called with null argument.")
        }

        emit(Response.Loading)
        val fireBaseUser = fbUser ?: run { emit(Response.Error()); return@flow }

        try {
            if (newUser!!) {
                val householdId = householdRepository.createHousehold()
                userRepository.createUser(householdId, fireBaseUser, registrationToken!!)
            } else {
                userRepository.addUserToken(fireBaseUser.uid, registrationToken!!)
            }
            userRepository.getUser() // get user to update cache
            emit(Response.Success(fbUser?.displayName.orEmpty()))
        } catch (e: FirebaseFirestoreException) {
            emit(Response.Error(errorHandler.getError(e)))
        }
    }.flowOn(Dispatchers.IO)
}
