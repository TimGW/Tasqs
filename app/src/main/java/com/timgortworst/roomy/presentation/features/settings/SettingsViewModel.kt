package com.timgortworst.roomy.presentation.features.settings

import androidx.lifecycle.*
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.user.GetUserUseCaseImpl
import com.timgortworst.roomy.presentation.base.model.EasterEgg
import com.timgortworst.roomy.domain.usecase.easteregg.EasterEggUseCaseImpl
import com.timgortworst.roomy.presentation.usecase.EasterEggUseCase
import com.timgortworst.roomy.presentation.usecase.GetUserUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val easterEggUseCase: EasterEggUseCase,
    getUserUseCase: GetUserUseCase
) : ViewModel() {

    private val _easterEgg = MutableLiveData<Response<EasterEgg?>>()
    val easterEgg: LiveData<Response<EasterEgg?>>
        get() = _easterEgg

    val currentUser =
        getUserUseCase.execute(
            GetUserUseCaseImpl.Params()
        ).asLiveData(viewModelScope.coroutineContext)

    fun onAppVersionClick(count: Int) {
        viewModelScope.launch {
            easterEggUseCase.execute(EasterEggUseCaseImpl.Params(count)).collect {
                _easterEgg.value = it
            }
        }
    }
}