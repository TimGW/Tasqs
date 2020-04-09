package com.timgortworst.roomy.presentation.features.settings

import androidx.lifecycle.*
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.usecase.user.GetUserUseCaseImpl
import com.timgortworst.roomy.presentation.base.model.EasterEgg
import com.timgortworst.roomy.domain.usecase.settings.EasterEggUseCaseImpl
import com.timgortworst.roomy.domain.usecase.settings.SetDarkModeUseCaseImpl
import com.timgortworst.roomy.presentation.usecase.SetDarkModeUseCase
import com.timgortworst.roomy.presentation.usecase.EasterEggUseCase
import com.timgortworst.roomy.presentation.usecase.GetDarkModeUseCase
import com.timgortworst.roomy.presentation.usecase.GetUserUseCase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val easterEggUseCase: EasterEggUseCase,
    private val setDarkModeUseCase: SetDarkModeUseCase,
    private val getDarkModeUseCase: GetDarkModeUseCase,
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

    fun getDarkModeSetting() = getDarkModeUseCase.execute().asLiveData()

    fun setDarkModeSetting(darkModeSetting: Int) {
        setDarkModeUseCase.execute(SetDarkModeUseCaseImpl.Params(darkModeSetting))
    }
}