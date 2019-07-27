package com.timgortworst.roomy.ui.main.presenter

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.timgortworst.roomy.model.Household
import com.timgortworst.roomy.repository.HouseholdRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.main.view.MainView
import com.timgortworst.roomy.utils.CoroutineLifecycleScope
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
}
