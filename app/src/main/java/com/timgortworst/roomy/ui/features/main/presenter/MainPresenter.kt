package com.timgortworst.roomy.ui.features.main.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.model.Household
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.domain.utils.CoroutineLifecycleScope
import com.timgortworst.roomy.ui.features.main.view.InviteLink
import com.timgortworst.roomy.ui.features.main.view.MainView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainPresenter
@Inject constructor(
        private val view: MainView,
        private val householdRepository: HouseholdRepository,
        private val userRepository: UserRepository
        ) : DefaultLifecycleObserver, HouseholdRepository.HouseholdListener {
    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun listenToHousehold() = scope.launch {
        householdRepository.listenToHousehold(
                userRepository.getHouseholdIdForUser(),
                this@MainPresenter
        )
    }

    fun detachHouseholdListener() {
        householdRepository.detachHouseholdListener()
    }

    override fun householdModified(household: Household) {
        if (household.userIdBlackList.contains(userRepository.getCurrentUserId())){
            view.logout()
        }
    }

    fun inviteUser() = scope.launch {
        view.share(userRepository.getHouseholdIdForUser())
    }

    fun buildInviteLink(householdId: String) {
        val linkUri = InviteLink.Builder()
                .householdId(householdId)
                .build()
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
