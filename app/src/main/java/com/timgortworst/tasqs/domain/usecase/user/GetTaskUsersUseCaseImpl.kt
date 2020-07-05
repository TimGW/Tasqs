package com.timgortworst.tasqs.domain.usecase.user

import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.tasqs.domain.model.Task
import com.timgortworst.tasqs.domain.model.response.ErrorHandler
import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.domain.usecase.None
import com.timgortworst.tasqs.presentation.usecase.user.GetAllUsersUseCase
import com.timgortworst.tasqs.presentation.usecase.user.GetTaskUsersUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GetTaskUsersUseCaseImpl(
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val errorHandler: ErrorHandler
) : GetTaskUsersUseCase {

    override fun execute(params: None) = flow {
        emit(Response.Loading)

        try {
            getAllUsersUseCase.execute(params).collect { response ->
                when (response) {
                    is Response.Success -> {
                        val result = response.data?.map { Task.User(it.userId, it.name) }
                        emit(Response.Success(result))
                    }
                }
            }
        } catch (e: FirebaseFirestoreException) {
            emit(Response.Error(errorHandler.getError(e)))
        }
    }.flowOn(Dispatchers.IO)
}