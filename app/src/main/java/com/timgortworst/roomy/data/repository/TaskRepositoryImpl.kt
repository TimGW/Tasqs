package com.timgortworst.roomy.data.repository

import com.google.firebase.firestore.*
import com.timgortworst.roomy.data.utils.CustomMapper
import com.timgortworst.roomy.domain.model.Household
import com.timgortworst.roomy.domain.model.Task
import com.timgortworst.roomy.domain.model.User.Companion.USER_ID_REF
import com.timgortworst.roomy.domain.model.firestore.TaskJson
import com.timgortworst.roomy.domain.model.firestore.TaskJson.Companion.TASK_META_DATA_REF
import com.timgortworst.roomy.domain.model.firestore.TaskJson.Companion.TASK_USER_REF
import com.timgortworst.roomy.domain.model.firestore.TaskMetaDataJson.Companion.TASK_DATE_TIME_REF
import com.timgortworst.roomy.domain.repository.TaskRepository
import com.timgortworst.roomy.domain.repository.UserRepository
import kotlinx.coroutines.tasks.await

class TaskRepositoryImpl(
    private val db: FirebaseFirestore,
    private val userRepository: UserRepository
): TaskRepository {

    private suspend fun taskCollection(): CollectionReference {
        return db.collection(Household.HOUSEHOLD_COLLECTION_REF)
            .document(userRepository.getUser(source = Source.CACHE)!!.householdId)
            .collection(TaskJson.TASK_COLLECTION_REF)
    }

    @Throws(FirebaseFirestoreException::class)
    override suspend fun createTask(task: Task): String? {
        val document = taskCollection().document()
        document.set(CustomMapper.convertToMap(task.apply { id = document.id })).await()
        return document.id
    }

    @Throws(FirebaseFirestoreException::class)
    override suspend fun updateTask(task: Task) {
        val document = taskCollection().document(task.id)
        document.update(CustomMapper.convertToMap(task)).await()
    }

    @Throws(FirebaseFirestoreException::class)
    override suspend fun getTasksForUser(userId: String): List<Task> {
        if (userId.isBlank()) return emptyList()

        return taskCollection()
            .whereEqualTo("$TASK_USER_REF.$USER_ID_REF", userId)
            .get()
            .await()
            .toObjects(TaskJson::class.java)
            .mapNotNull { CustomMapper.toTask(it) }
    }

    @Throws(FirebaseFirestoreException::class)
    override suspend fun getAllTasksQuery(): Query {
        return taskCollection()
            .orderBy("$TASK_META_DATA_REF.$TASK_DATE_TIME_REF", Query.Direction.ASCENDING)
    }

    @Throws(FirebaseFirestoreException::class)
    override suspend fun getTasksForUserQuery(userId: String): Query {
        return taskCollection()
            .whereEqualTo("$TASK_USER_REF.$USER_ID_REF", userId)
            .orderBy("$TASK_META_DATA_REF.$TASK_DATE_TIME_REF", Query.Direction.ASCENDING)
    }

    @Throws(FirebaseFirestoreException::class)
    override suspend fun updateTasks(tasks: List<Task>) {
        val batch = db.batch()
        tasks.forEach {
            batch.update(taskCollection().document(it.id), CustomMapper.convertToMap(it))
        }
        batch.commit().await()
    }

    @Throws(FirebaseFirestoreException::class)
    override suspend fun deleteTasks(tasks: List<Task>) {
        val batch = db.batch()
        tasks.forEach { batch.delete(taskCollection().document(it.id)) }
        batch.commit().await()
    }
}
