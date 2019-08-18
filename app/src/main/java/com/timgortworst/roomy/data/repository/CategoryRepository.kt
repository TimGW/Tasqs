package com.timgortworst.roomy.data.repository

import android.os.Handler
import android.util.Log
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import com.timgortworst.roomy.data.model.Category
import com.timgortworst.roomy.data.utils.Constants.CATEGORY_COLLECTION_REF
import com.timgortworst.roomy.data.utils.Constants.CATEGORY_DESCRIPTION_REF
import com.timgortworst.roomy.data.utils.Constants.CATEGORY_HOUSEHOLDID_REF
import com.timgortworst.roomy.data.utils.Constants.CATEGORY_ID_REF
import com.timgortworst.roomy.data.utils.Constants.CATEGORY_NAME_REF
import com.timgortworst.roomy.data.utils.Constants.LOADING_SPINNER_DELAY
import com.timgortworst.roomy.domain.ApiStatus
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val categoryCollectionRef = db.collection(CATEGORY_COLLECTION_REF)
    private var registration: ListenerRegistration? = null

    suspend fun createCategory(
            name: String,
            description: String,
            householdId: String
    ) {
        val document = categoryCollectionRef.document()

        val categoryFieldMap = mutableMapOf<String, Any>()
        categoryFieldMap[CATEGORY_ID_REF] = document.id
        if (name.isNotBlank()) categoryFieldMap[CATEGORY_NAME_REF] = name
        if (description.isNotBlank()) categoryFieldMap[CATEGORY_DESCRIPTION_REF] = description
        if (householdId.isNotBlank()) categoryFieldMap[CATEGORY_HOUSEHOLDID_REF] = householdId

        try {
            document.set(categoryFieldMap).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
        }
    }

    suspend fun createCategoryBatch(categoryList: MutableList<Category>) {
        val batch = db.batch()
        categoryList.forEach {
            val document = categoryCollectionRef.document()
            it.categoryId = document.id
            batch.set(document, it)
        }

        try {
            batch.commit().await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
        }
    }

    suspend fun getCategories(): List<Category> {
        return try {
            categoryCollectionRef.get(Source.CACHE).await().toObjects(Category::class.java)
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
            mutableListOf()
        }
    }

    fun listenToCategoriesForHousehold(householdId: String, apiStatus: ApiStatus) {
        val handler = Handler()
        val runnable = Runnable { apiStatus.setState(ApiStatus.Response.Loading) }
        handler.postDelayed(runnable, LOADING_SPINNER_DELAY)

        registration = categoryCollectionRef
                .whereEqualTo(CATEGORY_HOUSEHOLDID_REF, householdId)
                .addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
                    handler.removeCallbacks(runnable)
                    Log.d(TAG, "isFromCache: ${snapshots?.metadata?.isFromCache}")
                    when {
                        e != null && snapshots == null -> {
                            apiStatus.setState(ApiStatus.Response.Error(e))
                            Log.w(TAG, "listen:error", e)
                        }
                        else -> {
                            val changeList = snapshots?.documentChanges?.toList() ?: return@EventListener
                            val totalDataSetSize = snapshots.documents.toList().size
                            apiStatus.setState(ApiStatus.Response.Success(changeList, totalDataSetSize, snapshots.metadata.hasPendingWrites()))
                        }
                    }
                })
    }

    suspend fun updateCategory(
            categoryId: String,
            name: String? = null,
            description: String? = null,
            householdId: String? = null
    ) {
        val document = categoryCollectionRef.document(categoryId)

        val categoryFieldMap = mutableMapOf<String, Any>()
        categoryFieldMap[CATEGORY_ID_REF] = document.id
        name?.let { categoryFieldMap[CATEGORY_NAME_REF] = it }
        description?.let { categoryFieldMap[CATEGORY_DESCRIPTION_REF] = it }
        householdId?.let { categoryFieldMap[CATEGORY_HOUSEHOLDID_REF] = it }

        try {
            document.update(categoryFieldMap).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
        }
    }

    suspend fun deleteCategory(category: Category) {
        try {
            categoryCollectionRef
                    .document(category.categoryId)
                    .delete()
                    .await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
        }
    }

    fun detachCategoryListener() {
        registration?.remove()
    }

    companion object {
        private const val TAG = "CategoryRepository"
    }
}
