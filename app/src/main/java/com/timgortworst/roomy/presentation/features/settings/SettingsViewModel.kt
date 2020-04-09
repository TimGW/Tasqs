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

class SettingsViewModel(
    private val easterEggUseCase: EasterEggUseCase,
    getUserUseCase: GetUserUseCase
) : ViewModel() {

    private val _easterEgg = MutableLiveData<EasterEgg?>()
    val easterEgg: LiveData<EasterEgg?>
        get() = _easterEgg

    val currentUser =
        getUserUseCase.execute(
            GetUserUseCaseImpl.Params()
        ).asLiveData(viewModelScope.coroutineContext)

    fun onAppVersionClick(count: Int) {
        _easterEgg.value = easterEggUseCase.execute(EasterEggUseCaseImpl.Params(count))
    }
}