package com.timgortworst.roomy.data.repository

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.Household
import com.timgortworst.roomy.domain.model.User.Companion.USER_ID_REF
import com.timgortworst.roomy.domain.model.firestore.TaskJson
import com.timgortworst.roomy.domain.model.firestore.TaskJson.Companion.TASK_COLLECTION_REF
import com.timgortworst.roomy.domain.model.firestore.TaskJson.Companion.TASK_HOUSEHOLD_ID_REF
import com.timgortworst.roomy.domain.model.firestore.TaskJson.Companion.TASK_META_DATA_REF
import com.timgortworst.roomy.domain.model.firestore.TaskJson.Companion.TASK_USER_REF
import com.timgortworst.roomy.domain.model.firestore.TaskMetaDataJson.Companion.TASK_DATE_TIME_REF
import com.timgortworst.roomy.presentation.RoomyApp.Companion.TAG
import kotlinx.coroutines.tasks.await

class TaskRepository(private val idProvider: IdProvider) {

    private suspend fun collectionRef(): CollectionReference {
        return FirebaseFirestore
        .getInstance()
        .collection(Household.HOUSEHOLD_COLLECTION_REF)
        .document(idProvider.getHouseholdId())
        .collection(TASK_COLLECTION_REF)
    }

    suspend fun createTask(task: Task): String? {
        val document = collectionRef().document()

        return try {
            document.set(CustomMapper.convertToMap(task.apply { id = document.id })).await()
            document.id
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
            null
        }
    }

    suspend fun updateTask(task: Task) {
        val document = collectionRef().document(task.id)
        try {
            document.update(CustomMapper.convertToMap(task)).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
        }
    }

    suspend fun getTasksForUser(userId: String): List<Task> {
        if (userId.isBlank()) return emptyList()

        return try {
            collectionRef()
                .whereEqualTo("$TASK_USER_REF.$USER_ID_REF", userId)
                .get()
                .await()
                .toObjects(TaskJson::class.java)
                .mapNotNull { CustomMapper.toTask(it) }
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
            emptyList()
        }
    }

    suspend fun getAllTasksQuery(): Query {
        return collectionRef()
            .whereEqualTo(TASK_HOUSEHOLD_ID_REF, idProvider.getHouseholdId())
            .orderBy("$TASK_META_DATA_REF.$TASK_DATE_TIME_REF", Query.Direction.ASCENDING)
    }

    suspend fun updateTasks(tasks: List<Task>) {
        try {
            val batch = FirebaseFirestore.getInstance().batch()
            tasks.forEach {
                batch.update(collectionRef().document(it.id), CustomMapper.convertToMap(it))
            }
            batch.commit().await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
        }
    }

    suspend fun deleteTask(id: String) {
        try {
            collectionRef()
                .document(id)
                .delete()
                .await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
        }
    }

    suspend fun deleteTasks(tasks: List<Task>) {
        try {
            val batch = FirebaseFirestore.getInstance().batch()
            tasks.forEach { batch.delete(collectionRef().document(it.id)) }
            batch.commit().await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
        }
    }
}
