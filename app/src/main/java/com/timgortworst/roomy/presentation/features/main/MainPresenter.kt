package com.timgortworst.roomy.presentation.features.main

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.SharedPrefs
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.domain.model.Household
import com.timgortworst.roomy.domain.usecase.MainUseCase
import com.timgortworst.roomy.domain.usecase.UserUseCase
import com.timgortworst.roomy.presentation.base.CoroutineLifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainPresenter(
        private val view: MainView,
        private val mainUseCase: MainUseCase,
        private val userUseCase: UserUseCase,
        private val sharedPrefs: SharedPrefs
) : DefaultLifecycleObserver {
    private val scope = CoroutineLifecycleScope(Dispatchers.Main)
//    private val uId = FirebaseAuth.getInstance().currentUser?.uid

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

//    fun listenToHousehold() = scope.launch {
//        mainUseCase.listenToHousehold(this@MainPresenter)
//    }
//
//    fun detachHouseholdListener() {
//        mainUseCase.detachHouseholdListener()
//    }

//    override fun householdModified(household: Household) {
//        if (household.userIdBlackList.contains(uId)) {
//            view.logout()
//        }
//    }

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
            view.showToast(R.string.error_connection)
        }
    }

    fun showOrHideAd() = if (sharedPrefs.isAdsEnabled()) view.showAd() else view.hideAd()

    fun selectFragment() = scope.launch {
        if (userUseCase.getCurrentUser()?.email.isNullOrBlank()) {
            view.presentGoogleAuthFragment()
        } else {
            view.presentUsersFragment()
        }
    }
}
