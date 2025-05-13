package com.example.remindernotes.repository

import android.util.Log
import com.example.remindernotes.data.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalTime

class TaskRepository : ITaskRepository {
    private val col = FirebaseFirestore.getInstance().collection("tasks")

    override suspend fun getAll(): List<Task> =
        col.get().await().documents.map { it.toTask() }

    override suspend fun getTaskById(id: String): Task? =
        col.document(id).get().await().let { doc ->
            if (doc.exists()) {
                doc.toTask()
            } else {
                null
            }
        }

    override suspend fun insertTask(task: Task): Task {
        // 1) Reserve a new doc ID
        val docRef = col.document()
        // 2) Create a map of your task data
        val data = mapOf(
            "title"       to task.title,
            "description" to task.description,
            "dueDate"     to task.dueDate.toString(),
            "dueTime"     to task.dueTime.toString(),
            "userId"      to task.userId
        )
        // 3) Write it
        docRef.set(data).await()
        // 4) Return a Task with the new ID baked in
        Log.d("TaskRepository", "insertTask: ${docRef.id} ${task.title} ${task.description} ${task.dueDate} ${task.dueTime} ${task.userId}")
        return task.copy(id = docRef.id)
    }

    override suspend fun updateTask(task: Task) {
        // Now just overwrite the same document:
        col.document(task.id)
            .set(mapOf(
                "title"       to task.title,
                "description" to task.description,
                "dueDate"     to task.dueDate.toString(),
                "dueTime"     to task.dueTime.toString(),
                "userId"      to task.userId
            ))
            .await()
    }

    override suspend fun deleteTask(task: Task) {
        col.document(task.id).delete().await()
    }

    override suspend fun getTasksForUser(userId: Int): List<Task> =
        col.whereEqualTo("userId", userId)
            .get().await()
            .documents
            .map { it.toTask() }

    private fun DocumentSnapshot.toTask(): Task {
        val d = data!!
        return Task(
            id          = id,  // <-- Firestore doc ID
            title       = d["title"] as String,
            description = d["description"] as String,
            dueDate     = LocalDate.parse(d["dueDate"] as String),
            dueTime     = LocalTime.parse(d["dueTime"] as String),
            userId      = (d["userId"] as Long).toInt()
        )
    }
}
