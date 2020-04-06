package com.timgortworst.roomy.presentation.features.splash

import androidx.lifecycle.*
import com.timgortworst.roomy.domain.entity.response.Response
import com.timgortworst.roomy.domain.usecase.AppStartupUseCase
import com.timgortworst.roomy.presentation.base.model.StartUpAction
import com.timgortworst.roomy.domain.usecase.SwitchHouseholdUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SplashViewModel(
    private val switchHouseholdUseCase: SwitchHouseholdUseCase,
    private val appStartupUseCase: AppStartupUseCase
) : ViewModel() {

    private val _startupAction = MutableLiveData<Response<StartUpAction>>()
    val startupAction: LiveData<Response<StartUpAction>>
        get() = _startupAction

    fun handleAppStartup(referredHouseholdId: String) {
        viewModelScope.launch {
            appStartupUseCase.init(referredHouseholdId).invoke().collect {
                _startupAction.value = it
            }
        }
    }

    fun switchHousehold(newId: String) {
        viewModelScope.launch {
            switchHouseholdUseCase.init(newId).invoke().collect {
                _startupAction.value = it
            }
        }
    }
}