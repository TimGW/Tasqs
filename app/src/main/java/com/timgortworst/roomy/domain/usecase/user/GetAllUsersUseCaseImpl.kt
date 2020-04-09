package com.timgortworst.roomy.domain.usecase.user

import androidx.lifecycle.liveData
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Source
import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.repository.UserRepository
import com.timgortworst.roomy.presentation.usecase.GetAllUsersUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class GetAllUsersUseCaseImpl(
    private val userRepository: UserRepository,
    private val errorHandler: ErrorHandler
) : GetAllUsersUseCase {

    override fun execute(params: Unit?)= liveData(Dispatchers.IO) {
        val loadingJob = CoroutineScope(coroutineContext).launch {
            delay(500) // delay 0.5s before showing loading
            emit(Response.Loading)
        }
        try {
            val householdId = userRepository.getUser(source = Source.CACHE)?.householdId
                ?: run { emit(Response.Error()); return@liveData }

            emit(Response.Success(userRepository.getAllUsersForHousehold(householdId)))
        } catch (e: FirebaseFirestoreException) {
            emit(Response.Error(errorHandler.getError(e)))
        } finally {
            loadingJob.cancel()
        }
    }
}
