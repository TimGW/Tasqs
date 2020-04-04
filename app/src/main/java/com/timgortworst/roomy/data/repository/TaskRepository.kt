package com.timgortworst.roomy.data.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.timgortworst.roomy.domain.model.firestore.TaskJson
import com.timgortworst.roomy.domain.model.firestore.TaskJson.Companion.TASK_META_DATA_REF
import com.timgortworst.roomy.domain.model.firestore.TaskJson.Companion.TASK_USER_REF
import com.timgortworst.roomy.domain.model.firestore.TaskMetaDataJson.Companion.TASK_DATE_TIME_REF
import com.timgortworst.roomy.domain.model.firestore.User.Companion.USER_ID_REF
import com.timgortworst.roomy.domain.model.task.Task

interface TaskRepository {

    suspend fun taskCollection() : CollectionReference

    @Throws(FirebaseFirestoreException::class)
    suspend fun createTask(task: Task): String?

    @Throws(FirebaseFirestoreException::class)
    suspend fun updateTask(task: Task)

    @Throws(FirebaseFirestoreException::class)
    suspend fun getTasksForUser(userId: String): List<Task>

    @Throws(FirebaseFirestoreException::class)
    suspend fun getAllTasksQuery(): Query

    @Throws(FirebaseFirestoreException::class)
    suspend fun getTasksForUserQuery(userId: String): Query

    @Throws(FirebaseFirestoreException::class)
    suspend fun updateTasks(tasks: List<Task>)

    @Throws(FirebaseFirestoreException::class)
    suspend fun deleteTasks(tasks: List<Task>)
}
