package com.timgortworst.roomy.domain.usecase

import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.domain.UseCase
import com.timgortworst.roomy.domain.entity.User
import com.timgortworst.roomy.domain.entity.response.ErrorHandler
import com.timgortworst.roomy.domain.entity.response.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetUserUseCase(
    private val userRepository: UserRepository,
    private val errorHandler: ErrorHandler
) : UseCase<Flow<Response<User>>> {

    override fun executeUseCase() = flow {
        emit(Response.Loading)
        try {
            emit(Response.Success(userRepository.getUser()))
        } catch (e: FirebaseFirestoreException) {
            emit(Response.Error(errorHandler.getError(e)))
        }
    }.flowOn(Dispatchers.IO)
}

