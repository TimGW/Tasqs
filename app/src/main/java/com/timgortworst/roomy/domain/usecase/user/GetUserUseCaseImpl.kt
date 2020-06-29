package com.timgortworst.roomy.domain.usecase.user

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.repository.UserRepository
import com.timgortworst.roomy.presentation.usecase.user.GetUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetUserUseCaseImpl(
    private val userRepository: UserRepository,
    private val errorHandler: ErrorHandler
) : GetUserUseCase {

    data class Params(
        val source: Source = Source.DEFAULT,
        val userId: String? = FirebaseAuth.getInstance().currentUser?.uid
    )

    override fun execute(params: Params?) = flow {
        checkNotNull(params)
        emit(Response.Loading)

        try {
            val user = userRepository.getUser(params.userId, params.source)

            if (user != null) {
                emit(Response.Success(user))
            } else {
                emit(Response.Empty())
            }
        } catch (e: FirebaseFirestoreException) {
            emit(Response.Error(errorHandler.getError(e)))
        }
    }.flowOn(Dispatchers.IO)
}

