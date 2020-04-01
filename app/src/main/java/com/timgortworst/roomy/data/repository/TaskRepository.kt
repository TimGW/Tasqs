package com.timgortworst.roomy.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.User.Companion.USER_ID_REF
import com.timgortworst.roomy.domain.model.firestore.TaskJson
import com.timgortworst.roomy.domain.model.firestore.TaskJson.Companion.TASK_META_DATA_REF
import com.timgortworst.roomy.domain.model.firestore.TaskJson.Companion.TASK_USER_REF
import com.timgortworst.roomy.domain.model.firestore.TaskMetaDataJson.Companion.TASK_DATE_TIME_REF
import kotlinx.coroutines.tasks.await

class TaskRepository(
    private val db: FirebaseFirestore,
    private val householdRepository: HouseholdRepository
) {

    private suspend fun taskCollection() = householdRepository.taskCollection()

    @Throws(FirebaseFirestoreException::class)
    suspend fun createTask(task: Task): String? {
        val document = taskCollection().document()
        document.set(CustomMapper.convertToMap(task.apply { id = document.id })).await()
        return document.id
    }

    @Throws(FirebaseFirestoreException::class)
    suspend fun updateTask(task: Task) {
        val document = taskCollection().document(task.id)
        document.update(CustomMapper.convertToMap(task)).await()
    }

    @Throws(FirebaseFirestoreException::class)
    suspend fun getTasksForUser(userId: String): List<Task> {
        if (userId.isBlank()) return emptyList()

        return taskCollection()
            .whereEqualTo("$TASK_USER_REF.$USER_ID_REF", userId)
            .get()
            .await()
            .toObjects(TaskJson::class.java)
            .mapNotNull { CustomMapper.toTask(it) }
    }

    @Throws(FirebaseFirestoreException::class)
    suspend fun getAllTasksQuery(): Query {
        return taskCollection()
            .orderBy("$TASK_META_DATA_REF.$TASK_DATE_TIME_REF", Query.Direction.ASCENDING)
    }

    @Throws(FirebaseFirestoreException::class)
    suspend fun getTasksForUserQuery(userId: String): Query {
        return taskCollection()
            .whereEqualTo("$TASK_USER_REF.$USER_ID_REF", userId)
            .orderBy("$TASK_META_DATA_REF.$TASK_DATE_TIME_REF", Query.Direction.ASCENDING)
    }

    @Throws(FirebaseFirestoreException::class)
    suspend fun updateTasks(tasks: List<Task>) {
        val batch = db.batch()
        tasks.forEach {
            batch.update(taskCollection().document(it.id), CustomMapper.convertToMap(it))
        }
        batch.commit().await()
    }

    @Throws(FirebaseFirestoreException::class)
    suspend fun deleteTasks(tasks: List<Task>) {
        val batch = db.batch()
        tasks.forEach { batch.delete(taskCollection().document(it.id)) }
        batch.commit().await()
    }
}
