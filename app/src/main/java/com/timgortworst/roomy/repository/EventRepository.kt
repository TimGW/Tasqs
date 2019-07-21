package com.timgortworst.roomy.repository

import android.util.Log
import com.google.firebase.firestore.*
import com.google.firebase.firestore.DocumentChange.Type.*
import com.timgortworst.roomy.model.Category
import com.timgortworst.roomy.model.Event
import com.timgortworst.roomy.model.EventMetaData
import com.timgortworst.roomy.model.User
import com.timgortworst.roomy.utils.Constants.CATEGORIES_COLLECTION_REF
import com.timgortworst.roomy.utils.Constants.EVENT_CATEGORY_DESC_REF
import com.timgortworst.roomy.utils.Constants.EVENT_CATEGORY_ID_REF
import com.timgortworst.roomy.utils.Constants.EVENT_CATEGORY_NAME_REF
import com.timgortworst.roomy.utils.Constants.EVENT_CATEGORY_REF
import com.timgortworst.roomy.utils.Constants.EVENT_COLLECTION_REF
import com.timgortworst.roomy.utils.Constants.EVENT_INTERVAL_REF
import com.timgortworst.roomy.utils.Constants.EVENT_IS_DONE_REF
import com.timgortworst.roomy.utils.Constants.EVENT_META_DATA_REF
import com.timgortworst.roomy.utils.Constants.EVENT_START_DATE_REF
import com.timgortworst.roomy.utils.Constants.EVENT_USER_REF
import com.timgortworst.roomy.utils.Constants.HOUSEHOLD_COLLECTION_REF
import kotlinx.coroutines.tasks.await


class EventRepository(val userRepository: UserRepository) {
    val householdCollectionRef = FirebaseFirestore.getInstance().collection(HOUSEHOLD_COLLECTION_REF)
    private var eventListener: ListenerRegistration? = null
    private lateinit var categoryListener: ListenerRegistration

    suspend fun getCategories(): List<Category> {
        val document = householdCollectionRef.document(userRepository.getHouseholdIdForCurrentUser())
            .collection(CATEGORIES_COLLECTION_REF)
        return document.get().await().toObjects(Category::class.java)
    }

    suspend fun updateCategory(
        categoryId: String,
        name: String = "",
        description: String = ""
    ) {
        val document = householdCollectionRef.document(userRepository.getHouseholdIdForCurrentUser())
            .collection(CATEGORIES_COLLECTION_REF).document(categoryId)

        val categoryFieldMap = mutableMapOf<String, Any>()
        categoryFieldMap[EVENT_CATEGORY_ID_REF] = document.id
        if (name.isNotBlank()) categoryFieldMap[EVENT_CATEGORY_NAME_REF] = name
        if (description.isNotBlank()) categoryFieldMap[EVENT_CATEGORY_DESC_REF] = description

        try {
            document.update(categoryFieldMap).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e("TIMTIM", e.localizedMessage)
        }
    }

    suspend fun insertCategory(
        name: String = "",
        description: String = ""
    ) {
        val document = householdCollectionRef.document(userRepository.getHouseholdIdForCurrentUser())
            .collection(CATEGORIES_COLLECTION_REF).document()

        val categoryFieldMap = mutableMapOf<String, Any>()
        categoryFieldMap[EVENT_CATEGORY_ID_REF] = document.id
        if (name.isNotBlank()) categoryFieldMap[EVENT_CATEGORY_NAME_REF] = name
        if (description.isNotBlank()) categoryFieldMap[EVENT_CATEGORY_DESC_REF] = description

        try {
            document.set(categoryFieldMap).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e("TIMTIM", e.localizedMessage)
        }
    }

    suspend fun deleteCategoryForHousehold(category: Category) {
        try {
            householdCollectionRef.document(userRepository.getHouseholdIdForCurrentUser())
                .collection(CATEGORIES_COLLECTION_REF)
                .document(category.categoryId)
                .delete()
                .await()
        } catch (e: FirebaseFirestoreException) {
            Log.e("TIMTIM", e.localizedMessage)
        }
    }

    suspend fun getEvents(userIdFilter: String? = null): MutableList<Event>? {
        val query = householdCollectionRef.document(userRepository.getHouseholdIdForCurrentUser())
            .collection(EVENT_COLLECTION_REF)

        if (userIdFilter != null && userIdFilter.isNotEmpty()) {
            query.whereEqualTo("user.userId", userIdFilter)
        }

        query.orderBy("eventMetaData.repeatStartDate", Query.Direction.ASCENDING)
        return try {
            query.get().await().toObjects(Event::class.java)
        } catch (e: FirebaseFirestoreException) {
            throw e
        }
    }


    suspend fun insertAgendaEvent(
        category: Category,
        user: User,
        eventMetaData: EventMetaData,
        isEventDone: Boolean
    ) {
        val document = householdCollectionRef.document(userRepository.getHouseholdIdForCurrentUser())
            .collection(EVENT_COLLECTION_REF)
            .document()

        try {
            document.set(Event(document.id, category, user, eventMetaData, isEventDone)).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e("TIMTIM", e.localizedMessage)
        }
    }

    suspend fun updateAgendaEvent(
        eventId: String,
        category: Category? = null,
        user: User? = null,
        eventMetaData: EventMetaData? = null,
        isEventDone: Boolean? = null
    ) {
        val document = householdCollectionRef.document(userRepository.getHouseholdIdForCurrentUser())
            .collection(EVENT_COLLECTION_REF).document(eventId)

        val eventMetaDataMap = mutableMapOf<String, Any>()
        if (eventMetaData != null) eventMetaDataMap[EVENT_START_DATE_REF] = eventMetaData.repeatStartDate
        if (eventMetaData != null) eventMetaDataMap[EVENT_INTERVAL_REF] = eventMetaData.repeatInterval.name

        val eventFieldMap = mutableMapOf<String, Any>()
        if (category != null) eventFieldMap[EVENT_CATEGORY_REF] = category
        if (user != null) eventFieldMap[EVENT_USER_REF] = user
        if (eventMetaData != null) eventFieldMap[EVENT_META_DATA_REF] = eventMetaDataMap
        if (isEventDone != null) eventFieldMap[EVENT_IS_DONE_REF] = isEventDone

        try {
            document.update(eventFieldMap).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e("TIMTIM", e.localizedMessage)
        }
    }

    suspend fun removeAgendaEvent(eventId: String) {
        try {
            householdCollectionRef
                .document(userRepository.getHouseholdIdForCurrentUser())
                .collection(EVENT_COLLECTION_REF)
                .document(eventId)
                .delete()
                .await()
        } catch (e: FirebaseFirestoreException) {
            Log.e("TIMTIM", e.localizedMessage)
        }
    }

    suspend fun listenToCategories(taskListener: EventCategoryListener) {
        categoryListener = householdCollectionRef.document(userRepository.getHouseholdIdForCurrentUser())
            .collection(CATEGORIES_COLLECTION_REF)
            .addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
                if (e != null) {
                    Log.w(TAG, "listen:error", e)
                    return@EventListener
                }

                for (dc in snapshots!!.documentChanges) {
                    val eventCategory = dc.document.toObject(Category::class.java)
                    when (dc.type) {
                        ADDED -> {
                            taskListener.categoryAdded(eventCategory)
                        }
                        MODIFIED -> {
                            taskListener.categoryModified(eventCategory)
                        }
                        REMOVED -> {
                            taskListener.categoryDeleted(eventCategory)
                        }
                    }
                }
            })
    }

    suspend fun listenToEvents(agendaListener: AgendaEventListener) {
        val query = householdCollectionRef
            .document(userRepository.getHouseholdIdForCurrentUser())
            .collection(EVENT_COLLECTION_REF)

        query.orderBy("eventMetaData.repeatStartDate", Query.Direction.ASCENDING)

        eventListener = query.addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
            if (e != null && snapshots == null) {
                Log.w(TAG, "listen:error", e)
                return@EventListener
            }

            for (dc in snapshots!!.documentChanges) {
                val agendaEvent = dc.document.toObject(Event::class.java)
                when (dc.type) {
                    ADDED -> {
                        agendaListener.eventAdded(agendaEvent)
                    }
                    MODIFIED -> {
                        agendaListener.eventModified(agendaEvent)
                    }
                    REMOVED -> {
                        agendaListener.eventDeleted(agendaEvent)
                    }
                }
            }
        })
    }

    fun detachTaskListener() {
        categoryListener.remove()
    }

    fun detachEventListener() {
        eventListener?.remove()
    }

    companion object {
        private const val TAG = "EventCategoryRepository"
    }

    interface AgendaEventListener {
        fun eventAdded(agendaEvent: Event)
        fun eventModified(agendaEvent: Event)
        fun eventDeleted(agendaEvent: Event)
    }

    interface EventCategoryListener {
        fun categoryAdded(agendaEventCategory: Category)
        fun categoryModified(agendaEventCategory: Category)
        fun categoryDeleted(agendaEventCategory: Category)
    }
}
