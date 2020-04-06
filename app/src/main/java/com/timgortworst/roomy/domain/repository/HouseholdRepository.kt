package com.timgortworst.roomy.domain.repository

import com.google.firebase.firestore.FirebaseFirestoreException

interface HouseholdRepository {
    @Throws(FirebaseFirestoreException::class)
    suspend fun createHousehold(): String

    @Throws(FirebaseFirestoreException::class)
    suspend fun deleteHousehold(householdId: String)
}
