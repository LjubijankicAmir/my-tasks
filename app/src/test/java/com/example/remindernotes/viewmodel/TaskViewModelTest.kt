// src/test/java/com/example/remindernotes/viewmodel/TaskViewModelTest_Standard.kt
package com.example.remindernotes.viewmodel

import com.example.remindernotes.data.Task
import com.example.remindernotes.repository.ITaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {

    private class FakeTaskRepository : ITaskRepository {
        private val store = mutableListOf<Task>()
        override suspend fun getAll(): List<Task> = store.toList()
        override suspend fun insertTask(task: Task): Task {
            store += task
            return task
        }
        override suspend fun updateTask(task: Task) {
            val idx = store.indexOfFirst { it.id == task.id }
            if (idx >= 0) store[idx] = task
        }
        override suspend fun deleteTask(task: Task) {
            store.removeAll { it.id == task.id }
        }
        override suspend fun getTaskById(id: String): Task? =
            store.firstOrNull { it.id == id }
        override suspend fun getTasksForUser(userId: Int): List<Task> =
            store.filter { it.userId == userId }
    }

    private lateinit var viewModel: TaskViewModel
    private val dispatcher = StandardTestDispatcher()

    @Before fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = TaskViewModel(FakeTaskRepository())
    }

    @After fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test fun `init loads from repo`() = runTest {
        val fake = FakeTaskRepository()
        fake.insertTask(Task(id="1", title="T1", description="D", dueDate=java.time.LocalDate.now(), dueTime=java.time.LocalTime.NOON, userId=42))

        viewModel = TaskViewModel(fake)

        dispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.tasks.size)
        assertEquals("T1", viewModel.tasks[0].title)
    }

    @Test fun `add, edit, delete work`() = runTest {

        val t = Task("1","Title","Desc", java.time.LocalDate.now(), java.time.LocalTime.NOON, 7)
        viewModel.addTask(t)
        dispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.tasks.contains(t))

        val updated = t.copy(title="NewTitle")
        viewModel.editTask(updated)
        dispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.tasks.any { it.id=="1" && it.title=="NewTitle" })

        viewModel.deleteTask(updated)
        dispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.tasks.contains(updated))
    }
}
