package com.timgortworst.roomy.domain.usecase.user

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import com.timgortworst.roomy.domain.repository.UserRepository
import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.model.response.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetUserUseCase(
    private val userRepository: UserRepository,
    private val errorHandler: ErrorHandler
) : UseCase<Flow<Response<User>>, GetUserUseCase.Params> {

    data class Params(
        val source: Source = Source.DEFAULT,
        val userId: String? = FirebaseAuth.getInstance().currentUser?.uid
    )

    override fun execute(params: Params?) = flow {
        checkNotNull(params)

        emit(Response.Loading)
        try {
            emit(Response.Success(userRepository.getUser(params.userId, params.source)))
        } catch (e: FirebaseFirestoreException) {
            emit(Response.Error(errorHandler.getError(e)))
        }
    }.flowOn(Dispatchers.IO)
}

