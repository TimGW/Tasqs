package com.timgortworst.roomy.presentation.features.splash

import androidx.lifecycle.*
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.account.ValidationUseCaseImpl
import com.timgortworst.roomy.presentation.base.model.StartUpAction
import com.timgortworst.roomy.domain.usecase.household.SwitchHouseholdUseCaseImpl
import com.timgortworst.roomy.presentation.usecase.SwitchHouseholdUseCase
import com.timgortworst.roomy.presentation.usecase.ValidationUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SplashViewModel(
    private val switchHouseholdUseCase: SwitchHouseholdUseCase,
    private val appStartupUseCase: ValidationUseCase
) : ViewModel() {

    private val _startupAction = MutableLiveData<Response<StartUpAction>>()
    val startupAction: LiveData<Response<StartUpAction>>
        get() = _startupAction

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
}