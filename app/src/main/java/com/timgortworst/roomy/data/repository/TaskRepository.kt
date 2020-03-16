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

class TaskRepository(
    private val idProvider: IdProvider,
    private val db: FirebaseFirestore
) {

    private suspend fun taskCollection(): CollectionReference {
        return db
            .collection(Household.HOUSEHOLD_COLLECTION_REF)
            .document(idProvider.fetchHouseholdId())
            .collection(TASK_COLLECTION_REF)
    }

    suspend fun createTask(task: Task): String? {
        val document = taskCollection().document()

        return try {
            document.set(CustomMapper.convertToMap(task.apply { id = document.id })).await()
            document.id
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
            null
        }
    }

    suspend fun updateTask(task: Task) {
        val document = taskCollection().document(task.id)
        try {
            document.update(CustomMapper.convertToMap(task)).await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
        }
    }

    suspend fun getTasksForUser(userId: String): List<Task> {
        if (userId.isBlank()) return emptyList()

        return taskCollection()
            .whereEqualTo("$TASK_USER_REF.$USER_ID_REF", userId)
            .get()
            .await()
            .toObjects(TaskJson::class.java)
            .mapNotNull { CustomMapper.toTask(it) }
    }

    suspend fun getAllTasksQuery(): Query {
        return taskCollection()
            .whereEqualTo(TASK_HOUSEHOLD_ID_REF, idProvider.fetchHouseholdId())
            .orderBy("$TASK_META_DATA_REF.$TASK_DATE_TIME_REF", Query.Direction.ASCENDING)
    }

    suspend fun updateTasks(tasks: List<Task>) {
        try {
            val batch = db.batch()
            tasks.forEach {
                batch.update(taskCollection().document(it.id), CustomMapper.convertToMap(it))
            }
            batch.commit().await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
        }
    }

    suspend fun deleteTask(id: String) {
        try {
            taskCollection()
                .document(id)
                .delete()
                .await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
        }
    }

    suspend fun deleteTasks(tasks: List<Task>) {
        try {
            val batch = db.batch()
            tasks.forEach {
                batch.delete(taskCollection().document(it.id))
            }
            batch.commit().await()
        } catch (e: FirebaseFirestoreException) {
            Log.e(TAG, e.localizedMessage.orEmpty())
        }
    }
}
