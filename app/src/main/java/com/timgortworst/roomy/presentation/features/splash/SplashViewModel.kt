package com.timgortworst.roomy.presentation.features.splash

import androidx.lifecycle.*
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.usecase.account.ValidationUseCaseImpl
import com.timgortworst.roomy.domain.usecase.forcedupdate.ForcedUpdateUseCaseImpl
import com.timgortworst.roomy.presentation.base.model.StartUpAction
import com.timgortworst.roomy.domain.usecase.household.SwitchHouseholdUseCaseImpl
import com.timgortworst.roomy.presentation.base.model.UpdateAction
import com.timgortworst.roomy.presentation.usecase.household.SwitchHouseholdUseCase
import com.timgortworst.roomy.presentation.usecase.settings.ForcedUpdateUseCase
import com.timgortworst.roomy.presentation.usecase.signin.ValidationUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SplashViewModel(
    private val switchHouseholdUseCase: SwitchHouseholdUseCase,
    private val appStartupUseCase: ValidationUseCase,
    private val forcedUpdateUseCase: ForcedUpdateUseCase
) : ViewModel() {

    private val _startupAction = MutableLiveData<Response<StartUpAction>>()
    val startupAction: LiveData<Response<StartUpAction>>
        get() = _startupAction

    private val _updateAction = MutableLiveData<Response<UpdateAction>>()
    val updateAction: LiveData<Response<UpdateAction>>
        get() = _updateAction

    fun handleAppStartup(referredHouseholdId: String) {
        viewModelScope.launch {
            appStartupUseCase.execute(ValidationUseCaseImpl.Params(referredHouseholdId)).collect {
                _startupAction.value = it
            }
        }
    }

    fun switchHousehold(newId: String) {
        viewModelScope.launch {
            switchHouseholdUseCase.execute(SwitchHouseholdUseCaseImpl.Params(newId)).collect {
                _startupAction.value = it
            }
        }
    }

    fun checkForUpdates(currentVersion: String) {
        viewModelScope.launch {
            forcedUpdateUseCase.execute(ForcedUpdateUseCaseImpl.Params(currentVersion)).collect {
                _updateAction.value = it
            }
        }
    }
}