package com.timgortworst.roomy.domain.usecase.user

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.repository.UserRepository
import com.timgortworst.roomy.presentation.usecase.user.AddTokenUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AddTokenUseCaseImpl(
    private val userRepository: UserRepository,
    private val errorHandler: ErrorHandler
) : AddTokenUseCase {

    data class Params(val token: String)

    override fun execute(params: Params?) = flow {
        checkNotNull(params)

        emit(Response.Loading)

        try {
            val fbUser = FirebaseAuth.getInstance().currentUser
            userRepository.addUserToken(fbUser?.uid, params.token)
            emit(Response.Success())
        } catch (e: FirebaseFirestoreException) {
            emit(Response.Error(errorHandler.getError(e)))
        }
    }.flowOn(Dispatchers.IO)
}
