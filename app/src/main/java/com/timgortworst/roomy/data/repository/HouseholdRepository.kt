package com.timgortworst.roomy.data.repository

import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.timgortworst.roomy.domain.model.Household
import com.timgortworst.roomy.domain.model.Household.Companion.HOUSEHOLD_BLACKLIST_REF
import com.timgortworst.roomy.domain.model.Household.Companion.HOUSEHOLD_COLLECTION_REF
import com.timgortworst.roomy.domain.model.User.Companion.USER_HOUSEHOLD_ID_REF
import com.timgortworst.roomy.presentation.RoomyApp.Companion.TAG
import kotlinx.coroutines.tasks.await

class HouseholdRepository {
    private val householdsCollectionRef = FirebaseFirestore.getInstance().collection(HOUSEHOLD_COLLECTION_REF)
//    private var registration: ListenerRegistration? = null

    suspend fun createHousehold(): String? {
        val household = householdsCollectionRef.document()
        return try {
            household.set(Household(householdId = household.id)).await()
            household.id
        } catch (e: FirebaseFirestoreException) {
            null
        }
    }

//    suspend fun getHousehold(householdId: String): Household? {
//        val housholdRef = householdsCollectionRef.document(householdId)
//        return try {
//            housholdRef.get().await().toObject(Household::class.java)
//        } catch (e: FirebaseFirestoreException) {
//            null
//        }
//    }

//    fun listenToHousehold(householdId: String?, householdListener: HouseholdListener) {
//        if (householdId.isNullOrEmpty()) return
//
//        registration = householdsCollectionRef
//                .whereEqualTo(USER_HOUSEHOLD_ID_REF, householdId)
//                .addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
//                    if (e != null && snapshots == null) {
//                        Log.e(TAG, "listen:error", e)
//                        return@EventListener
//                    }
//                    Log.d(TAG, "isFromCache: ${snapshots?.metadata?.isFromCache}")
//                    for (dc in snapshots!!.documentChanges) {
//                        val household = dc.document.toObject(Household::class.java)
//                        when (dc.type) {
//                            DocumentChange.Type.MODIFIED -> householdListener.householdModified(household)
//                            else -> { }
//                        }
//                    }
//                })
//    }

//    suspend fun updateHousehold(
//        householdId: String?,
//        blackList: MutableList<String>?
//    ) {
//        householdId ?: return
//        val householdDocRef = householdsCollectionRef.document(householdId)
//
//        val fieldMap = mutableMapOf<String, Any>()
//        fieldMap[USER_HOUSEHOLD_ID_REF] = householdId
//        blackList?.let { fieldMap[HOUSEHOLD_BLACKLIST_REF] = it }
//
//        try {
//            householdDocRef.set(fieldMap, SetOptions.merge()).await()
//        } catch (e: FirebaseFirestoreException) {
//            Log.e(TAG, e.localizedMessage.orEmpty())
//        }
//    }

    suspend fun deleteHousehold(householdId: String) {
        try {
            householdsCollectionRef.document(householdId).delete().await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
        }
    }
//
//    fun detachHouseholdListener() {
//        registration?.remove()
//    }

//    interface HouseholdListener {
//        fun householdModified(household: Household)
//    }
}
