package com.timgortworst.tasqs.presentation.features.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timgortworst.tasqs.domain.model.response.Response
import com.timgortworst.tasqs.domain.usecase.splash.ForcedUpdateUseCaseImpl
import com.timgortworst.tasqs.domain.usecase.splash.SwitchHouseholdUseCaseImpl
import com.timgortworst.tasqs.domain.usecase.splash.ValidationUseCaseImpl
import com.timgortworst.tasqs.domain.usecase.task.AppStartupNotificationUseCaseImpl
import com.timgortworst.tasqs.presentation.base.model.Event
import com.timgortworst.tasqs.presentation.base.model.StartUpAction
import com.timgortworst.tasqs.presentation.base.model.UpdateAction
import com.timgortworst.tasqs.presentation.usecase.splash.ForcedUpdateUseCase
import com.timgortworst.tasqs.presentation.usecase.splash.SwitchHouseholdUseCase
import com.timgortworst.tasqs.presentation.usecase.splash.ValidationUseCase
import com.timgortworst.tasqs.presentation.usecase.task.AppStartupNotificationUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SplashViewModel(
    private val switchHouseholdUseCase: SwitchHouseholdUseCase,
    private val validationUseCase: ValidationUseCase,
    private val forcedUpdateUseCase: ForcedUpdateUseCase,
    private val appStartupNotificationUseCase: AppStartupNotificationUseCase
) : ViewModel() {

    private val _startupAction = MutableLiveData<Event<Response<StartUpAction>>>()
    val startupAction: LiveData<Event<Response<StartUpAction>>>
        get() = _startupAction

    private val _updateAction = MutableLiveData<Event<Response<UpdateAction>>>()
    val updateAction: LiveData<Event<Response<UpdateAction>>>
        get() = _updateAction

    fun handleAppStartup(referredHouseholdId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            validationUseCase.execute(ValidationUseCaseImpl.Params(referredHouseholdId)).collect {
                _startupAction.postValue(Event(it))
            }

            appStartupNotificationUseCase.execute(AppStartupNotificationUseCaseImpl.Params()).collect()
        }
    }

    fun switchHousehold(newId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            switchHouseholdUseCase.execute(SwitchHouseholdUseCaseImpl.Params(newId)).collect {
                _startupAction.postValue(Event(it))
            }
        }
    }

    fun checkForUpdates(currentVersion: String) {
        viewModelScope.launch(Dispatchers.IO) {
            forcedUpdateUseCase.execute(ForcedUpdateUseCaseImpl.Params(currentVersion)).collect {
                _updateAction.postValue(Event(it))
            }
        }
    }
}