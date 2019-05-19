package com.timgortworst.roomy.ui.main.presenter

import com.timgortworst.roomy.R
import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import com.timgortworst.roomy.model.User
import com.timgortworst.roomy.repository.HouseholdRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.main.view.MainView
import kotlinx.coroutines.InternalCoroutinesApi


class MainPresenter(
    private val view: MainView,
    private val sharedPref: HuishoudGenootSharedPref,
    private val householdRepository: HouseholdRepository,
    private val userRepository: UserRepository
) {
    private lateinit var currentUser: User

    fun handleMenuItemClick(itemId: Int) {
        when (itemId) {
            R.id.home -> {
                view.presentAgendaFragment()
            }
            R.id.householdTasks -> {
                view.presentTasksFragment()
            }
            R.id.profile -> {
                view.presentProfileActivity()
            }
        }
    }

    fun loadHouseHold() {

    }
}
