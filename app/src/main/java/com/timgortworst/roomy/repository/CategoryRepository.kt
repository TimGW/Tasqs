package com.timgortworst.roomy.repository

import android.util.Log
import com.google.firebase.firestore.DocumentChange.Type.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import com.timgortworst.roomy.model.Category
import com.timgortworst.roomy.utils.Constants.CATEGORIES_COLLECTION_REF
import com.timgortworst.roomy.utils.Constants.EVENT_CATEGORY_DESC_REF
import com.timgortworst.roomy.utils.Constants.EVENT_CATEGORY_ID_REF
import com.timgortworst.roomy.utils.Constants.EVENT_CATEGORY_NAME_REF
import com.timgortworst.roomy.utils.Constants.HOUSEHOLD_COLLECTION_REF
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(private val userRepository: UserRepository) {
    private val householdCollectionRef = FirebaseFirestore.getInstance().collection(HOUSEHOLD_COLLECTION_REF)
    private var categoryListener: ListenerRegistration? = null

    suspend fun createCategory(
            name: String = "",
            description: String = ""
    ) {
        val document = householdCollectionRef.document(userRepository.readHouseholdIdForCurrentUser())
                .collection(CATEGORIES_COLLECTION_REF).document()

        val categoryFieldMap = mutableMapOf<String, Any>()
        categoryFieldMap[EVENT_CATEGORY_ID_REF] = document.id
        if (name.isNotBlank()) categoryFieldMap[EVENT_CATEGORY_NAME_REF] = name
        if (description.isNotBlank()) categoryFieldMap[EVENT_CATEGORY_DESC_REF] = description

        try {
            document.set(categoryFieldMap).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e("TIMTIM", e.localizedMessage!!)
        }
    }

    suspend fun readCategories(): List<Category> {
        val document = householdCollectionRef.document(userRepository.readHouseholdIdForCurrentUser())
                .collection(CATEGORIES_COLLECTION_REF)
        return document.get(Source.CACHE).await().toObjects(Category::class.java)
    }

    suspend fun updateCategory(
            categoryId: String,
            name: String = "",
            description: String = ""
    ) {
        val document = householdCollectionRef.document(userRepository.readHouseholdIdForCurrentUser())
                .collection(CATEGORIES_COLLECTION_REF).document(categoryId)

        val categoryFieldMap = mutableMapOf<String, Any>()
        categoryFieldMap[EVENT_CATEGORY_ID_REF] = document.id
        if (name.isNotBlank()) categoryFieldMap[EVENT_CATEGORY_NAME_REF] = name
        if (description.isNotBlank()) categoryFieldMap[EVENT_CATEGORY_DESC_REF] = description

        try {
            document.update(categoryFieldMap).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e("TIMTIM", e.localizedMessage!!)
        }
    }

    suspend fun deleteCategory(category: Category) {
        try {
            householdCollectionRef.document(userRepository.readHouseholdIdForCurrentUser())
                    .collection(CATEGORIES_COLLECTION_REF)
                    .document(category.categoryId)
                    .delete()
                    .await()
        } catch (e: FirebaseFirestoreException) {
            Log.e("TIMTIM", e.localizedMessage!!)
        }
    }

    suspend fun listenToCategories(taskListener: CategoryListener) {
        taskListener.setLoading(true)

        categoryListener = householdCollectionRef.document(userRepository.readHouseholdIdForCurrentUser())
                .collection(CATEGORIES_COLLECTION_REF)
                .addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
                    if (e != null) {
                        taskListener.setLoading(false)
                        Log.w(TAG, "listen:error", e)
                        return@EventListener
                    }

                    for (dc in snapshots!!.documentChanges) {
                        val eventCategory = dc.document.toObject(Category::class.java)
                        when (dc.type) {
                            ADDED -> taskListener.categoryAdded(eventCategory)
                            MODIFIED -> taskListener.categoryModified(eventCategory)
                            REMOVED -> taskListener.categoryDeleted(eventCategory)
                        }
                    }
                    taskListener.setLoading(false)
                })
    }

    fun detachCategoryListener() {
        categoryListener?.remove()
    }

    companion object {
        private const val TAG = "CategoryRepository"
    }

    interface CategoryListener : ObjectStateListener {
        fun categoryAdded(category: Category)
        fun categoryModified(category: Category)
        fun categoryDeleted(category: Category)
    }
}
