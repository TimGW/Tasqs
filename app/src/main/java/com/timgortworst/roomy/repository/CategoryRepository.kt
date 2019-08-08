package com.timgortworst.roomy.repository

import android.os.Handler
import android.util.Log
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.Source
import com.timgortworst.roomy.model.Category
import com.timgortworst.roomy.utils.Constants.CATEGORY_COLLECTION_REF
import com.timgortworst.roomy.utils.Constants.CATEGORY_DESCRIPTION_REF
import com.timgortworst.roomy.utils.Constants.CATEGORY_HOUSEHOLDID_REF
import com.timgortworst.roomy.utils.Constants.CATEGORY_ID_REF
import com.timgortworst.roomy.utils.Constants.CATEGORY_NAME_REF
import com.timgortworst.roomy.utils.Constants.LOADING_SPINNER_DELAY
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()
    var categoryCollectionRef = db.collection(CATEGORY_COLLECTION_REF)
        private set

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
            batch.set(categoryCollectionRef.document(), it)
        }
        batch.commit().await()
    }

    suspend fun getCategories(): List<Category> {
        return categoryCollectionRef.get(Source.CACHE).await().toObjects(Category::class.java)
    }

    fun listenToCategoriesForHousehold(householdId: String, baseResponse: BaseResponse) {
        val handler = Handler()
        val runnable = Runnable { baseResponse.setResponse(DataListener.Loading) }
        handler.postDelayed(runnable, LOADING_SPINNER_DELAY)

        registration = categoryCollectionRef
                .whereEqualTo(CATEGORY_HOUSEHOLDID_REF, householdId)
                .addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
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

    suspend fun updateCategory(
            categoryId: String,
            name: String = "",
            description: String = "",
            householdId: String = ""
    ) {
        val document = categoryCollectionRef.document(categoryId)

        val categoryFieldMap = mutableMapOf<String, Any>()
        categoryFieldMap[CATEGORY_ID_REF] = document.id
        if (name.isNotBlank()) categoryFieldMap[CATEGORY_NAME_REF] = name
        if (description.isNotBlank()) categoryFieldMap[CATEGORY_DESCRIPTION_REF] = description
        if (householdId.isNotBlank()) categoryFieldMap[CATEGORY_HOUSEHOLDID_REF] = description

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
