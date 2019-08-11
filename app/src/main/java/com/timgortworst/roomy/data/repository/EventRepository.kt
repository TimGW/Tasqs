package com.timgortworst.roomy.data.repository

import android.os.Handler
import android.util.Log
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.QuerySnapshot
import com.timgortworst.roomy.data.model.Category
import com.timgortworst.roomy.data.model.Event
import com.timgortworst.roomy.data.model.EventMetaData
import com.timgortworst.roomy.data.model.User
import com.timgortworst.roomy.domain.utils.Constants.EVENT_CATEGORY_REF
import com.timgortworst.roomy.domain.utils.Constants.EVENT_COLLECTION_REF
import com.timgortworst.roomy.domain.utils.Constants.EVENT_HOUSEHOLD_ID_REF
import com.timgortworst.roomy.domain.utils.Constants.EVENT_INTERVAL_REF
import com.timgortworst.roomy.domain.utils.Constants.EVENT_META_DATA_REF
import com.timgortworst.roomy.domain.utils.Constants.EVENT_START_DATE_REF
import com.timgortworst.roomy.domain.utils.Constants.EVENT_USER_REF
import com.timgortworst.roomy.domain.utils.Constants.LOADING_SPINNER_DELAY
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class EventRepository @Inject constructor() {
    var eventCollectionRef = FirebaseFirestore.getInstance().collection(EVENT_COLLECTION_REF)
        private set

    private var registration: ListenerRegistration? = null

    suspend fun createEvent(
            eventMetaData: EventMetaData,
            category: Category,
            user: User,
            householdId: String
    ) : String {
        val document = eventCollectionRef.document()
        return try {
            document.set(Event(document.id, eventMetaData, category, user, householdId)).await()
            document.id
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
            ""
        }
    }

    suspend fun getEventsForUser(userId: String): MutableList<Event> {
        return eventCollectionRef.whereEqualTo("user.userId", userId).get().await().toObjects(Event::class.java)
    }

    fun listenToEventsForHousehold(householdId: String, baseResponse: BaseResponse) {
        val handler = Handler()
        val runnable = Runnable { baseResponse.setResponse(DataListener.Loading) }
        handler.postDelayed(runnable, LOADING_SPINNER_DELAY)

        registration = eventCollectionRef
                .whereEqualTo(EVENT_HOUSEHOLD_ID_REF, householdId)
                .addSnapshotListener(MetadataChanges.INCLUDE, EventListener<QuerySnapshot> { snapshots, e ->
                    handler.removeCallbacks(runnable)
                    Log.d(TAG, "isFromCache: ${snapshots?.metadata?.isFromCache}")
                    when {
                        e != null && snapshots == null -> {
                            baseResponse.setResponse(DataListener.Error(e))
                            Log.w(TAG, "listen:error", e)
                        }
                        else -> {
                            val changeList = snapshots?.documentChanges?.toList() ?: return@EventListener
                            val totalDataSetSize = snapshots.documents.toList().size

                            baseResponse.setResponse(DataListener.Success(changeList, totalDataSetSize, snapshots.metadata.hasPendingWrites()))
                        }
                    }
                })
    }

    suspend fun updateEvent(
            eventId: String,
            eventMetaData: EventMetaData? = null,
            category: Category? = null,
            user: User? = null,
            householdId: String? = null
    ) {
        val document = eventCollectionRef.document(eventId)

        val eventMetaDataMap = mutableMapOf<String, Any>()
        if (eventMetaData != null) eventMetaDataMap[EVENT_START_DATE_REF] = eventMetaData.nextEventDate
        if (eventMetaData != null) eventMetaDataMap[EVENT_INTERVAL_REF] = eventMetaData.repeatInterval.name

        val eventFieldMap = mutableMapOf<String, Any>()
        if (category != null) eventFieldMap[EVENT_CATEGORY_REF] = category
        if (user != null) eventFieldMap[EVENT_USER_REF] = user
        if (eventMetaData != null) eventFieldMap[EVENT_META_DATA_REF] = eventMetaDataMap
        if (householdId != null) eventFieldMap[EVENT_HOUSEHOLD_ID_REF] = householdId

        try {
            document.update(eventFieldMap).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
        }
    }

    suspend fun deleteEvent(eventId: String) {
        try {
            eventCollectionRef
                    .document(eventId)
                    .delete()
                    .await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage!!)
        }
    }

    fun detachEventListener() {
        registration?.remove()
    }

    companion object {
        private const val TAG = "EventRepository"
    }
}
