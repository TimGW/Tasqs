package com.timgortworst.tasqs.domain.usecase.splash

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.tasqs.domain.model.response.ErrorHandler
import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.domain.repository.UserRepository
import com.timgortworst.tasqs.presentation.base.model.StartUpAction
import com.timgortworst.tasqs.presentation.usecase.splash.ValidationUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ValidationUseCaseImpl(
    private val userRepository: UserRepository,
    private val errorHandler: ErrorHandler
) : ValidationUseCase {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    data class Params(val referredId: String)

    override fun execute(params: Params?) = flow {
        checkNotNull(params)
        emit(Response.Loading)

        try {
            val currentId = fetchHouseholdId() // fetch here to update local cache
            emit(validate(currentId, params.referredId))
        } catch (e: FirebaseFirestoreException) {
            emit(Response.Error(errorHandler.getError(e)))
        }
    }.flowOn(Dispatchers.IO)

    private fun validate(currentId: String, referredId: String): Response<StartUpAction> {
        return when {
            // first check if user has valid authentication
            (auth.currentUser == null ||
                    auth.currentUser?.uid?.isBlank() == true) -> {
                Response.Success(StartUpAction.TriggerSignInFlow)
            }

            // check if the user accepted an invite link
            referredId.isNotBlank() -> Response.Success(referredUser(currentId, referredId))

            // continue to the main activity
            else -> Response.Success(StartUpAction.TriggerMainFlow)
        }
    }

    private fun referredUser(currentId: String, referredId: String): StartUpAction {
        return when (referredId) {
            currentId -> StartUpAction.DialogSameId // user is already in the same household
            else -> StartUpAction.DialogOverrideId(referredId) // user will override
        }
    }

    private suspend fun fetchHouseholdId() = userRepository.getUser()?.householdId.orEmpty()
}
