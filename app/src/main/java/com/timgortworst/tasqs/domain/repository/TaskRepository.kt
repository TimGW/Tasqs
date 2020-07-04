package com.timgortworst.tasqs.domain.repository

import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.timgortworst.tasqs.domain.model.Task

interface TaskRepository {

    @Throws(FirebaseFirestoreException::class)
    suspend fun createTask(task: Task): String?

    @Throws(FirebaseFirestoreException::class)
    suspend fun updateTask(task: Task)

    @Throws(FirebaseFirestoreException::class)
    suspend fun getTasksForUser(userId: String): List<Task>

    @Throws(FirebaseFirestoreException::class)
    suspend fun getAllTasksQuery(): FirestoreRecyclerOptions.Builder<Task>

    @Throws(FirebaseFirestoreException::class)
    suspend fun getTasksForUserQuery(userId: String): FirestoreRecyclerOptions.Builder<Task>

    @Throws(FirebaseFirestoreException::class)
    suspend fun updateTasks(tasks: List<Task>)

    @Throws(FirebaseFirestoreException::class)
    suspend fun deleteTasks(tasks: List<Task>)

    @Throws(FirebaseFirestoreException::class)
    suspend fun getTask(taskId: String) : Task?
}
