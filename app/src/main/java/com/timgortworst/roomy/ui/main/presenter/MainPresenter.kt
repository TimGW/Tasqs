package com.timgortworst.roomy.ui.main.presenter

import com.timgortworst.roomy.R
import com.timgortworst.roomy.local.HuishoudGenootSharedPref
import com.timgortworst.roomy.repository.HouseholdRepository
import com.timgortworst.roomy.repository.UserRepository
import com.timgortworst.roomy.ui.main.view.MainView


class MainPresenter(
    private val view: MainView,
    private val sharedPref: HuishoudGenootSharedPref,
    private val householdRepository: HouseholdRepository,
    private val userRepository: UserRepository
) {
    fun handleMenuItemClick(itemId: Int) {
        when (itemId) {
            R.id.home -> {
                view.presentAgendaFragment()
            }
            R.id.household_task_list -> {
                view.presentTasksFragment()
            }
            R.id.profile -> {
                view.presentHousematesFragment()
            }
        }
    }
}
