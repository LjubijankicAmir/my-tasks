package com.example.remindernotes.viewmodel

import FakeUserPreferences
import FakeUserRepository
import com.example.remindernotes.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest {

    private lateinit var viewModel: UserViewModel
    private lateinit var fakeRepo: FakeUserRepository
    private lateinit var fakePrefs: FakeUserPreferences

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepo = FakeUserRepository()
        fakePrefs = FakeUserPreferences()
        viewModel = UserViewModel(fakeRepo, fakePrefs)
    }

    @After
    fun tearDown() {
        viewModel.onCleared()
        Dispatchers.resetMain()
    }

    @Test
    fun registerThrowsIfUserAlreadyExists() = runTest {
        val user = User(
            name     = "Amir",
            surname  = "Test",
            email    = "amir@test.com",
            password = "12345678"
        )
        fakeRepo.register(user)

        try {
            viewModel.register(user)
            fail("Expected IllegalArgumentException when registering an existing user")
        } catch (e: IllegalArgumentException) {
            assertEquals("User already exists", e.message)
        }
    }

    @Test
    fun loginSucceedsWithValidCredentials() = runTest {
        val user = User(
            name     = "Amir",
            surname  = "Test",
            email    = "amir@test.com",
            password = "12345678"
        )
        fakeRepo.register(user)

        val result = viewModel.login(
            email    = "amir@test.com",
            password = "12345678"
        )

        assertNotNull("login() should return a User on success", result)
        assertEquals(user.email, result!!.email)
        assertTrue(fakePrefs.isUserLoggedIn())
        assertEquals(user.id, fakePrefs.getLoggedInUserId())
        assertEquals(user, viewModel.loggedInUser.value)
    }

    @Test
    fun loginFailsWithInvalidCredentials() = runTest {
        val user = User(
            name     = "Amir",
            surname  = "Test",
            email    = "amir@test.com",
            password = "12345678"
        )
        fakeRepo.register(user)

        val result = viewModel.login(
            email    = "amir@test.com",
            password = "wrongpass"
        )

        assertNull("login() should return null on bad credentials", result)
        assertFalse(fakePrefs.isUserLoggedIn())
        assertEquals(-1, fakePrefs.getLoggedInUserId())
        assertEquals(
            "Invalid email or password",
            viewModel.loginStatusMessage.value
        )
    }
}
