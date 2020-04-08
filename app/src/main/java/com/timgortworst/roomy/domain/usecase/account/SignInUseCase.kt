package com.timgortworst.roomy.domain.usecase.account

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.iid.FirebaseInstanceId
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.repository.HouseholdRepository
import com.timgortworst.roomy.domain.repository.UserRepository
import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.model.response.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class SignInUseCase(
    private val householdRepository: HouseholdRepository,
    private val userRepository: UserRepository,
    private val errorHandler: ErrorHandler,
    private val fbAuth: FirebaseAuth,
    private val fbInstanceId: FirebaseInstanceId
) : UseCase<Flow<Response<String>>, SignInUseCase.Params> {

    data class Params(val newUser: Boolean)

    override fun execute(params: Params?) = flow {
        checkNotNull(params)

        emit(Response.Loading)
        val fireBaseUser = fbAuth.currentUser ?: run { emit(Response.Error()); return@flow }
        val token = fbInstanceId.instanceId.await().token

        try {
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
