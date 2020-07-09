package com.timgortworst.tasqs.data.repository

import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.*
import com.timgortworst.tasqs.data.mapper.HouseholdDataMapper.Companion.HOUSEHOLD_COLLECTION_REF
import com.timgortworst.tasqs.data.mapper.ListMapper
import com.timgortworst.tasqs.data.mapper.Mapper
import com.timgortworst.tasqs.data.mapper.TaskDataMapper.Companion.TASK_COLLECTION_REF
import com.timgortworst.tasqs.data.mapper.TaskDataMapper.Companion.TASK_DATE_TIME_REF
import com.timgortworst.tasqs.data.mapper.TaskDataMapper.Companion.TASK_META_DATA_REF
import com.timgortworst.tasqs.data.mapper.TaskDataMapper.Companion.TASK_USER_REF
import com.timgortworst.tasqs.data.mapper.TaskDataMapper.Companion.USER_ID_REF
import com.timgortworst.tasqs.domain.model.Task
import com.timgortworst.tasqs.domain.repository.TaskRepository
import com.timgortworst.tasqs.domain.repository.UserRepository
import kotlinx.coroutines.tasks.await

class TaskRepositoryImpl(
    private val userRepository: UserRepository,
    private val taskDataMapper: Mapper<Map<String, Any>, Task>,
    private val taskListMapper: ListMapper<Map<String, Any>, Task>
) : TaskRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private suspend fun taskCollection(): CollectionReference {
        return db.collection(HOUSEHOLD_COLLECTION_REF)
            .document(userRepository.getUser(source = Source.CACHE)!!.householdId)
            .collection(TASK_COLLECTION_REF)
    }

    @Throws(FirebaseFirestoreException::class)
    override suspend fun createTask(task: Task): String? {
        val document = taskCollection().document()

        val domainTask = task.apply { id = document.id }
        val networkTask = taskDataMapper.mapOutgoing(domainTask)

        document.set(networkTask).await()
        return document.id
    }

    @Throws(FirebaseFirestoreException::class)
    override suspend fun updateTask(task: Task) {
        val document = taskCollection().document(task.id!!)
        document.update(taskDataMapper.mapOutgoing(task)).await()
    }

    @Throws(FirebaseFirestoreException::class)
    override suspend fun getTasksForUser(userId: String): List<Task> {
        if (userId.isBlank()) return emptyList()

        val networkTaskList: List<Map<String, Any>> = taskCollection()
            .whereEqualTo("$TASK_USER_REF.$USER_ID_REF", userId)
            .get()
            .await()
            .documents.mapNotNull { it.data }

        return taskListMapper.mapIncoming(networkTaskList)
    }

    @Throws(FirebaseFirestoreException::class)
    override suspend fun getAllTasksQuery(): FirestoreRecyclerOptions.Builder<Task> {
        val query = taskCollection()
            .orderBy("$TASK_META_DATA_REF.$TASK_DATE_TIME_REF", Query.Direction.ASCENDING)

        return FirestoreRecyclerOptions.Builder<Task>()
            .setQuery(query) {
                taskDataMapper.mapIncoming(it.data.orEmpty())
            }
    }

    @Throws(FirebaseFirestoreException::class)
    override suspend fun getTasksForUserQuery(userId: String): FirestoreRecyclerOptions.Builder<Task> {
        val query = taskCollection()
            .whereEqualTo("$TASK_USER_REF.$USER_ID_REF", userId)
            .orderBy("$TASK_META_DATA_REF.$TASK_DATE_TIME_REF", Query.Direction.ASCENDING)

        return FirestoreRecyclerOptions.Builder<Task>()
            .setQuery(query) {
                taskDataMapper.mapIncoming(it.data.orEmpty())
            }
    }

    @Throws(FirebaseFirestoreException::class)
    override suspend fun updateTasks(tasks: List<Task>) {
        val batch = db.batch()
        tasks.forEach {
            batch.update(taskCollection().document(it.id!!), taskDataMapper.mapOutgoing(it))
        }
        batch.commit().await()
    }

    @Throws(FirebaseFirestoreException::class)
    override suspend fun deleteTasks(tasks: List<Task>) {
        val batch = db.batch()
        tasks.forEach { batch.delete(taskCollection().document(it.id!!)) }
        batch.commit().await()
    }

    @Throws(FirebaseFirestoreException::class)
    override suspend fun getTask(taskId: String): Task? {
        val networkTask = taskCollection()
            .document(taskId)
            .get()
            .await()
            .data.orEmpty()

        return taskDataMapper.mapIncoming(networkTask)
    }
}
