package com.timgortworst.roomy.presentation.features.main.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.model.Household
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.domain.usecase.MainUseCase
import com.timgortworst.roomy.presentation.base.CoroutineLifecycleScope
import com.timgortworst.roomy.presentation.features.main.view.MainView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainPresenter
@Inject constructor(
        private val view: MainView,
        private val mainUseCase: MainUseCase
) : DefaultLifecycleObserver, HouseholdRepository.HouseholdListener {
    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun listenToHousehold() = scope.launch {
        mainUseCase.listenToHousehold(this@MainPresenter)
    }

    fun detachHouseholdListener() {
        mainUseCase.detachHouseholdListener()
    }

    override fun householdModified(household: Household) {
        if (household.userIdBlackList.contains(mainUseCase.getCurrentUserId())) {
            view.logout()
        }
    }

    fun inviteUser() = scope.launch {
        view.share(mainUseCase.getHouseholdIdForUser())
    }

    fun buildInviteLink(householdId: String) {
        val linkUri = mainUseCase.buildInviteLink(householdId)
        view.presentShareLinkUri(linkUri)
    }

    fun networkStatusChanged(isEnabled: Boolean) {
        if (isEnabled) {
            view.loadAd()
        } else {
            view.showToast(R.string.connection_error)
        }
    }
}
