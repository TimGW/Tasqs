package com.timgortworst.roomy.presentation.features.main

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.timgortworst.roomy.R
import com.timgortworst.roomy.data.SharedPrefs
import com.timgortworst.roomy.domain.usecase.UserUseCase
import com.timgortworst.roomy.domain.utils.InviteLinkBuilder
import com.timgortworst.roomy.presentation.base.CoroutineLifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainPresenter(
        private val view: MainView,
        private val userUseCase: UserUseCase,
        private val sharedPrefs: SharedPrefs
) : DefaultLifecycleObserver {
    private val scope = CoroutineLifecycleScope(Dispatchers.Main)

    init {
        if (view is LifecycleOwner) {
            view.lifecycle.addObserver(scope)
        }
    }

    fun inviteUser() = scope.launch {
        view.share(userUseCase.getHouseholdIdForUser())
    }

    fun buildInviteLink(householdId: String) {
        val linkUri = InviteLinkBuilder.Builder().householdId(householdId).build()
        view.presentShareLinkUri(linkUri)
    }

    fun networkStatusChanged(isEnabled: Boolean) {
        if (isEnabled) {
            view.loadAd()
        } else {
            view.showToast(R.string.error_connection)
        }
    }

    fun showOrHideAd() = if (sharedPrefs.isAdsEnabled()) view.showAdContainer() else view.hideAdContainer()
}
