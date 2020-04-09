package com.timgortworst.roomy.domain.usecase.user

import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.repository.UserRepository
import com.timgortworst.roomy.domain.usecase.SuspendUseCase
import com.timgortworst.roomy.presentation.RoomyApp
import com.timgortworst.roomy.presentation.RoomyApp.Companion.LOADING_DELAY
import com.timgortworst.roomy.presentation.usecase.AddTokenUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class AddTokenUseCaseImpl(
    private val userRepository: UserRepository,
    private val errorHandler: ErrorHandler
) : AddTokenUseCase {

    data class Params(val token: String)

    override fun execute(params: Params?) = flow {
        checkNotNull(params)

        val loadingJob = CoroutineScope(coroutineContext).launch {
            delay(LOADING_DELAY)
            emit(Response.Loading)
        }

        try {
            userRepository.addUserToken(userRepository.getFbUser()?.uid, params.token)
            emit(Response.Success())
        } catch (e: FirebaseFirestoreException) {
            emit(Response.Error(errorHandler.getError(e)))
        }finally {
            loadingJob.cancel()
        }
    }.flowOn(Dispatchers.IO)
}
