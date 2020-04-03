package com.timgortworst.roomy.domain.usecase

import androidx.lifecycle.liveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.data.sharedpref.SharedPrefs
import com.timgortworst.roomy.domain.model.response.ErrorHandler
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.model.ui.EasterEgg
import com.timgortworst.roomy.presentation.base.Event
import com.timgortworst.roomy.presentation.features.settings.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class SettingsUseCase(
    private val userRepository: UserRepository,
    private val sharedPrefs: SharedPrefs,
    private val errorHandler: ErrorHandler
) {

    fun getCurrentUser() = liveData(Dispatchers.IO) {
        val loadingJob = CoroutineScope(coroutineContext).launch {
            delay(500) // delay 0.5s before showing loading
            emit(Event(Response.Loading))
        }

        try {
            emit(Event(Response.Success(userRepository.getUser(FirebaseAuth.getInstance().currentUser?.uid))))
        } catch (e: FirebaseFirestoreException) {
            emit(Event(Response.Error(errorHandler.getError(e))))
        } finally {
            loadingJob.cancel()
        }
    }

    private fun betweenUntil(comparable: Int, x: Int, y: Int): Boolean = (comparable in x until y)

    fun onAppVersionClick(counter: Int): EasterEgg? {
        if (sharedPrefs.isAdsEnabled()) {
            return when {
                betweenUntil(counter, CLICKS_FOR_MESSAGE, CLICKS_FOR_EASTER_EGG) -> {
                    EasterEgg(R.string.easter_egg_message, (CLICKS_FOR_EASTER_EGG - counter))
                }
                counter == CLICKS_FOR_EASTER_EGG -> {
                    sharedPrefs.setAdsEnabled(false)
                    EasterEgg(R.string.easter_egg_enabled)
                }
                else -> null
            }
        } else {
            return if (counter == CLICKS_FOR_EASTER_EGG) {
                EasterEgg(R.string.easter_egg_already_enabled)
            } else {
                null
            }
        }
    }

    companion object {
        private const val CLICKS_FOR_EASTER_EGG: Int = 10
        private const val CLICKS_FOR_MESSAGE: Int = 7
    }
}
