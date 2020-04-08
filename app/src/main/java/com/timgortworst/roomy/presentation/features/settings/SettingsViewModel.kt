package com.timgortworst.roomy.presentation.features.settings

import androidx.lifecycle.*
import com.timgortworst.roomy.domain.model.User
import com.timgortworst.roomy.domain.model.response.Response
import com.timgortworst.roomy.domain.usecase.UseCase
import com.timgortworst.roomy.domain.usecase.user.GetUserUseCase
import com.timgortworst.roomy.presentation.base.model.EasterEgg
import com.timgortworst.roomy.domain.usecase.easteregg.EasterEggUseCase
import kotlinx.coroutines.flow.Flow

class SettingsViewModel(
    private val easterEggUseCase: UseCase<EasterEgg?, EasterEggUseCase.Params>,
    getUserUseCase: UseCase<Flow<Response<User>>, GetUserUseCase.Params>
) : ViewModel() {

    private val _easterEgg = MutableLiveData<EasterEgg?>()
    val easterEgg: LiveData<EasterEgg?>
        get() = _easterEgg

    val currentUser =
        getUserUseCase.execute(
            GetUserUseCase.Params()
        ).asLiveData(viewModelScope.coroutineContext)

    fun onAppVersionClick(count: Int) {
        _easterEgg.value = easterEggUseCase.execute(EasterEggUseCase.Params(count))
    }
}