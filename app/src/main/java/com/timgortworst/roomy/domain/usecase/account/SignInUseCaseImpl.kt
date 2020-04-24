package com.timgortworst.roomy.domain.usecase.account

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.iid.FirebaseInstanceId
import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.repository.HouseholdRepository
import com.timgortworst.roomy.domain.repository.UserRepository
import com.timgortworst.roomy.presentation.RoomyApp
import com.timgortworst.roomy.presentation.usecase.signin.SignInUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignInUseCaseImpl(
    private val householdRepository: HouseholdRepository,
    private val userRepository: UserRepository,
    private val errorHandler: ErrorHandler,
    private val fbAuth: FirebaseAuth,
    private val fbInstanceId: FirebaseInstanceId
) : SignInUseCase {

    data class Params(val newUser: Boolean)

    override fun execute(params: Params?) = channelFlow {
        checkNotNull(params)

        val loadingJob = CoroutineScope(coroutineContext).launch {
            delay(RoomyApp.LOADING_DELAY)
            offer(Response.Loading)
        }

        val fireBaseUser = fbAuth.currentUser ?: run { offer(Response.Error()); return@channelFlow }
        val token = fbInstanceId.instanceId.await().token

        try {
            if (params.newUser) {
                val householdId = householdRepository.createHousehold()
                userRepository.createUser(householdId, fireBaseUser, token)
            } else {
                userRepository.addUserToken(fireBaseUser.uid, token)
            }
            userRepository.getUser() // get user to update cache
            offer(Response.Success(fireBaseUser.displayName.orEmpty()))
        } catch (e: FirebaseFirestoreException) {
            offer(Response.Error(errorHandler.getError(e)))
        } finally {
            loadingJob.cancel()
        }
    }.flowOn(Dispatchers.IO)
}
