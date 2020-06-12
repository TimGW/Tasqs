package com.timgortworst.roomy.presentation.features.splash

import androidx.lifecycle.*
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.usecase.splash.ValidationUseCaseImpl
import com.timgortworst.roomy.domain.usecase.splash.ForcedUpdateUseCaseImpl
import com.timgortworst.roomy.presentation.base.model.StartUpAction
import com.timgortworst.roomy.domain.usecase.splash.SwitchHouseholdUseCaseImpl
import com.timgortworst.roomy.presentation.base.model.Event
import com.timgortworst.roomy.presentation.base.model.UpdateAction
import com.timgortworst.roomy.presentation.usecase.splash.SwitchHouseholdUseCase
import com.timgortworst.roomy.presentation.usecase.splash.ForcedUpdateUseCase
import com.timgortworst.roomy.presentation.usecase.splash.ValidationUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SplashViewModel(
    private val switchHouseholdUseCase: SwitchHouseholdUseCase,
    private val appStartupUseCase: ValidationUseCase,
    private val forcedUpdateUseCase: ForcedUpdateUseCase
) : ViewModel() {

    private val _startupAction = MutableLiveData<Event<Response<StartUpAction>>>()
    val startupAction: LiveData<Event<Response<StartUpAction>>>
        get() = _startupAction

    private val _updateAction = MutableLiveData<Event<Response<UpdateAction>>>()
    val updateAction: LiveData<Event<Response<UpdateAction>>>
        get() = _updateAction

    fun handleAppStartup(referredHouseholdId: String) {
        viewModelScope.launch {
            appStartupUseCase.execute(ValidationUseCaseImpl.Params(referredHouseholdId)).collect {
                _startupAction.value = Event(it)
            }
        }
    }

    fun switchHousehold(newId: String) {
        viewModelScope.launch {
            switchHouseholdUseCase.execute(SwitchHouseholdUseCaseImpl.Params(newId)).collect {
                _startupAction.value = Event(it)
            }
        }
    }

    fun checkForUpdates(currentVersion: String) {
        viewModelScope.launch {
            forcedUpdateUseCase.execute(ForcedUpdateUseCaseImpl.Params(currentVersion)).collect {
                _updateAction.value = Event(it)
            }
        }
    }
}