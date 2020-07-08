package com.timgortworst.tasqs.domain.usecase.signin

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.iid.FirebaseInstanceId
import com.timgortworst.tasqs.domain.model.response.ErrorHandler
import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.domain.repository.HouseholdRepository
import com.timgortworst.tasqs.domain.repository.UserRepository
import com.timgortworst.tasqs.presentation.usecase.signin.SignInUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class SignInUseCaseImpl(
    private val householdRepository: HouseholdRepository,
    private val userRepository: UserRepository,
    private val errorHandler: ErrorHandler
) : SignInUseCase {
    private val fbAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val fbInstanceId: FirebaseInstanceId = FirebaseInstanceId.getInstance()

    data class Params(val newUser: Boolean)

    override fun execute(params: Params) = flow {
        emit(Response.Loading)

        val fireBaseUser = fbAuth.currentUser ?: run { emit(Response.Error()); return@flow }

        try {
            val instanceId = fbInstanceId.instanceId.await()
            val token = instanceId.token

            if (params.newUser) {
                val householdId = householdRepository.createHousehold()
                userRepository.createUser(householdId, fireBaseUser, token)
            } else {
                userRepository.addUserToken(fireBaseUser.uid, token)
            }
            userRepository.getUser() // get user to update cache
            emit(Response.Success(fireBaseUser.displayName.orEmpty()))
        } catch (e: FirebaseFirestoreException) {
            emit(Response.Error(errorHandler.getError(e)))
        }
    }.flowOn(Dispatchers.IO)
}
