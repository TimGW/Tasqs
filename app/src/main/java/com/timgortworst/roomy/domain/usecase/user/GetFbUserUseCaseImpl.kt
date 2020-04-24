package com.timgortworst.roomy.domain.usecase.user

import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.repository.UserRepository
import com.timgortworst.roomy.presentation.RoomyApp.Companion.LOADING_DELAY
import com.timgortworst.roomy.presentation.usecase.user.GetFbUserUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class GetFbUserUseCaseImpl(
    private val userRepository: UserRepository,
    private val errorHandler: ErrorHandler
) : GetFbUserUseCase {

    override fun execute(params: Unit?) = flow {
        val loadingJob = CoroutineScope(coroutineContext).launch {
            delay(LOADING_DELAY)
            emit(Response.Loading)
        }

        try {
            emit(Response.Success(userRepository.getFbUser()))
        } catch (e: FirebaseFirestoreException) {
            emit(Response.Error(errorHandler.getError(e)))
        } finally {
            loadingJob.cancel()
        }
    }.flowOn(Dispatchers.IO)
}
