package com.timgortworst.roomy.domain.usecase.user

import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.repository.UserRepository
import com.timgortworst.roomy.presentation.RoomyApp.Companion.LOADING_DELAY
import com.timgortworst.roomy.presentation.usecase.settings.AddTokenUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class AddTokenUseCaseImpl(
    private val userRepository: UserRepository,
    private val errorHandler: ErrorHandler
) : AddTokenUseCase {

    data class Params(val token: String)

    override fun execute(params: Params?) = channelFlow {
        checkNotNull(params)

        val loadingJob = CoroutineScope(coroutineContext).launch {
            delay(LOADING_DELAY)
            offer(Response.Loading)
        }

        try {
            userRepository.addUserToken(userRepository.getFbUser()?.uid, params.token)
            offer(Response.Success())
        } catch (e: FirebaseFirestoreException) {
            offer(Response.Error(errorHandler.getError(e)))
        }finally {
            loadingJob.cancel()
        }
    }.flowOn(Dispatchers.IO)
}
