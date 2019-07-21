package com.timgortworst.roomy.repository

import android.util.Log
import com.google.firebase.firestore.*
import com.google.firebase.firestore.DocumentChange.Type.*
import com.timgortworst.roomy.model.Category
import com.timgortworst.roomy.utils.Constants.CATEGORIES_COLLECTION_REF
import com.timgortworst.roomy.utils.Constants.EVENT_CATEGORY_DESC_REF
import com.timgortworst.roomy.utils.Constants.EVENT_CATEGORY_ID_REF
import com.timgortworst.roomy.utils.Constants.EVENT_CATEGORY_NAME_REF
import com.timgortworst.roomy.utils.Constants.HOUSEHOLD_COLLECTION_REF
import kotlinx.coroutines.tasks.await

class CategoryRepository(val userRepository: UserRepository) {
    private val householdCollectionRef = FirebaseFirestore.getInstance().collection(HOUSEHOLD_COLLECTION_REF)
    private var categoryListener: ListenerRegistration? = null

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
            Log.e("TIMTIM", e.localizedMessage!!)
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
            Log.e("TIMTIM", e.localizedMessage!!)
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
            Log.e("TIMTIM", e.localizedMessage!!)
        }
    }

    suspend fun listenToCategories(taskListener: CategoryListener) {
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
                        ADDED -> taskListener.categoryAdded(eventCategory)
                        MODIFIED -> taskListener.categoryModified(eventCategory)
                        REMOVED -> taskListener.categoryDeleted(eventCategory)
                    }
                }
            })
    }

    fun detachCategoryListener() {
        categoryListener?.remove()
    }

    companion object {
        private const val TAG = "CategoryRepository"
    }

    interface CategoryListener {
        fun categoryAdded(category: Category)
        fun categoryModified(category: Category)
        fun categoryDeleted(category: Category)
    }
}
