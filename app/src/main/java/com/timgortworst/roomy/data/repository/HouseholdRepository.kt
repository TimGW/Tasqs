package com.timgortworst.roomy.data.repository

import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.timgortworst.roomy.data.model.Household
import com.timgortworst.roomy.domain.utils.Constants
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HouseholdRepository @Inject constructor() {
    val householdsCollectionRef = FirebaseFirestore.getInstance().collection(Constants.HOUSEHOLD_COLLECTION_REF)
    private var registration: ListenerRegistration? = null

    suspend fun createHousehold(): String? {
        val household = householdsCollectionRef.document()
        return try {
            household.set(Household(householdId = household.id)).await()
            household.id
        } catch (e: FirebaseFirestoreException) {
            null
        }
    }

    fun listenToHousehold(householdId: String?, householdListener: HouseholdListener) {
        if (householdId.isNullOrEmpty()) return

        registration = householdsCollectionRef
                .whereEqualTo(Constants.USER_HOUSEHOLDID_REF, householdId)
                .addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
                    if (e != null && snapshots == null) {
                        Log.w(TAG, "listen:error", e)
                        return@EventListener
                    }
                    Log.d(TAG, "isFromCache: ${snapshots?.metadata?.isFromCache}")
                    for (dc in snapshots!!.documentChanges) {
                        val household = dc.document.toObject(Household::class.java)
                        when (dc.type) {
                            DocumentChange.Type.MODIFIED -> householdListener.householdModified(household)
                            else -> { }
                        }
                    }
                })
    }

    suspend fun updateHousehold(
        householdId: String,
        blackList: MutableList<String> = mutableListOf()
    ) {
        val householdDocRef = householdsCollectionRef.document(householdId)

        val fieldMap = mutableMapOf<String, Any>()
        if (householdId.isNotBlank()) fieldMap[Constants.HOUSEHOLD_ID_REF] = householdId
        if (blackList.isNotEmpty()) fieldMap[Constants.HOUSEHOLD_BLACKLIST_REF] = blackList

        householdDocRef.set(fieldMap, SetOptions.merge()).await()
    }

    suspend fun deleteHousehold(householdId: String) {
        householdsCollectionRef.document(householdId).delete().await()
    }

    companion object {
        private const val TAG = "HouseholdRepository"
    }

    fun detachHouseholdListener() {
        registration?.remove()
    }

    interface HouseholdListener {
        fun householdModified(household: Household)
    }
}
