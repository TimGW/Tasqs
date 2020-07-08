package com.timgortworst.tasqs.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.timgortworst.tasqs.data.mapper.HouseholdDataMapper.Companion.HOUSEHOLD_COLLECTION_REF
import com.timgortworst.tasqs.data.mapper.Mapper
import com.timgortworst.tasqs.domain.model.Household
import com.timgortworst.tasqs.domain.repository.HouseholdRepository
import kotlinx.coroutines.tasks.await

class HouseholdRepositoryImpl(
    private val householdDataMapper: Mapper<Map<String, Any>, Household>
) : HouseholdRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val householdCollection = db.collection(HOUSEHOLD_COLLECTION_REF)

    override suspend fun createHousehold(): String {
        val household = householdCollection.document()
        val domainHousehold = Household(householdId = household.id)
        household.set(householdDataMapper.mapOutgoing(domainHousehold)).await()
        return household.id
    }

    override suspend fun deleteHousehold(householdId: String) {
        householdCollection.document(householdId).delete().await()
    }
}
