package com.timgortworst.roomy.presentation.features.settings

import androidx.lifecycle.*
import com.timgortworst.roomy.domain.application.user.GetUserUseCase
import com.timgortworst.roomy.presentation.base.model.EasterEgg
import com.timgortworst.roomy.domain.application.EasterEggUseCase

class SettingsViewModel(
    private val easterEggUseCase: EasterEggUseCase,
    getUserUseCase: GetUserUseCase
) : ViewModel() {

    private val _easterEgg = MutableLiveData<EasterEgg?>()
    val easterEgg: LiveData<EasterEgg?>
        get() = _easterEgg

    val currentUser = getUserUseCase.invoke().asLiveData(viewModelScope.coroutineContext)

    fun onAppVersionClick(count: Int) {
        _easterEgg.value = easterEggUseCase.init(count).invoke()
    }
}