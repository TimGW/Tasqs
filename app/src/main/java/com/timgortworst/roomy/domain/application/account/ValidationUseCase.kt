package com.timgortworst.roomy.domain.application.account

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.domain.application.UseCase
import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.presentation.base.model.StartUpAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ValidationUseCase(
    private val userRepository: UserRepository,
    private val errorHandler: ErrorHandler
) : UseCase<Flow<Response<StartUpAction>>> {
    private val auth = FirebaseAuth.getInstance()
    lateinit var referredId: String

    fun init(householdId: String): ValidationUseCase {
        this.referredId = householdId
        return this
    }

    override fun invoke() = flow {
        emit(Response.Loading)
        try {
            val currentId = fetchHouseholdId() // fetch here to update local cache
            emit(validate(currentId, referredId))
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
