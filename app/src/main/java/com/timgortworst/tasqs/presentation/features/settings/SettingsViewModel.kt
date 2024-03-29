package com.timgortworst.tasqs.presentation.features.settings

import androidx.lifecycle.*
import com.timgortworst.tasqs.data.sharedpref.SharedPrefs
import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.domain.usecase.user.GetUserUseCaseImpl
import com.timgortworst.tasqs.presentation.base.model.EasterEgg
import com.timgortworst.tasqs.domain.usecase.settings.EasterEggUseCaseImpl
import com.timgortworst.tasqs.infrastructure.notifications.NotificationQueue
import com.timgortworst.tasqs.presentation.usecase.settings.EasterEggUseCase
import com.timgortworst.tasqs.presentation.usecase.user.GetUserUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val easterEggUseCase: EasterEggUseCase,
    private val sharedPrefs: SharedPrefs,
    getUserUseCase: GetUserUseCase,
    private val notificationQueue: NotificationQueue
) : ViewModel() {

    private val _easterEgg = MutableLiveData<Response<EasterEgg?>>()
    val easterEgg: LiveData<Response<EasterEgg?>>
        get() = _easterEgg

    val currentUser = getUserUseCase
        .execute(GetUserUseCaseImpl.Params())
        .asLiveData(viewModelScope.coroutineContext)

    fun onAppVersionClick(count: Int) {
        viewModelScope.launch {
            easterEggUseCase.execute(EasterEggUseCaseImpl.Params(count)).collect {
                _easterEgg.value = it
            }
        }
    }

    fun getDarkModeSetting() = sharedPrefs.getDarkModeSetting()

    fun setDarkModeSetting(darkModeSetting: Int) {
        sharedPrefs.setDarkModeSetting(darkModeSetting)
    }

    fun removeAllNotifications() {
        notificationQueue.removeAllPendingNotifications()
    }
}