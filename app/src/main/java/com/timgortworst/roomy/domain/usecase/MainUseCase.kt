package com.timgortworst.roomy.domain.usecase

import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.domain.utils.InviteLinkBuilder
import com.timgortworst.roomy.presentation.features.main.presenter.MainPresenter

class MainUseCase(private val householdRepository: HouseholdRepository,
                  private val userRepository: UserRepository) {
    suspend fun listenToHousehold(mainPresenter: MainPresenter) {
        householdRepository.listenToHousehold(
                userRepository.getHouseholdIdForUser(userRepository.getCurrentUserId()),
                mainPresenter
        )
    }

    fun detachHouseholdListener() {
        householdRepository.detachHouseholdListener()
    }

    fun getCurrentUserId() = userRepository.getCurrentUserId()

    suspend fun getHouseholdIdForUser() = userRepository.getHouseholdIdForUser(userRepository.getCurrentUserId())

    fun buildInviteLink(householdId: String) = InviteLinkBuilder.Builder().householdId(householdId).build()
}
