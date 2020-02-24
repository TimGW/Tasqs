package com.timgortworst.roomy.domain.usecase

import com.google.firebase.auth.FirebaseAuth
import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.UserRepository
import com.timgortworst.roomy.domain.utils.InviteLinkBuilder
import com.timgortworst.roomy.presentation.features.main.presenter.MainPresenter

class MainUseCase(private val householdRepository: HouseholdRepository,
                  private val userRepository: UserRepository) {
    private val uId = FirebaseAuth.getInstance().currentUser?.uid

    suspend fun listenToHousehold(mainPresenter: MainPresenter) {
        householdRepository.listenToHousehold(
                userRepository.getHouseholdIdForUser(uId),
                mainPresenter
        )
    }

    fun detachHouseholdListener() {
        householdRepository.detachHouseholdListener()
    }

    suspend fun getHouseholdIdForUser() = userRepository.getHouseholdIdForUser(uId)

    fun buildInviteLink(householdId: String) = InviteLinkBuilder.Builder().householdId(householdId).build()
}
