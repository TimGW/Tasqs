package com.timgortworst.roomy.domain.usecase

import com.timgortworst.roomy.data.repository.HouseholdRepository
import com.timgortworst.roomy.data.repository.IdProvider
import com.timgortworst.roomy.domain.utils.InviteLinkBuilder

class MainUseCase(private val householdRepository: HouseholdRepository,
                  private val idProvider: IdProvider) {
//    private val uId = FirebaseAuth.getInstance().currentUser?.uid

//    suspend fun listenToHousehold(mainPresenter: MainPresenter) {
//        householdRepository.listenToHousehold(
//                userRepository.getHouseholdIdForUser(uId),
//                mainPresenter
//        )
//    }

//    fun detachHouseholdListener() {
//        householdRepository.detachHouseholdListener()
//    }

    suspend fun getHouseholdIdForUser() = idProvider.getHouseholdId()

    fun buildInviteLink(householdId: String) = InviteLinkBuilder.Builder().householdId(householdId).build()
}
