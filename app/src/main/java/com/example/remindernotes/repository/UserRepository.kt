package com.example.remindernotes.repository


import com.example.remindernotes.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.tasks.await

class UserRepository : IUserRepository {
    private val auth = FirebaseAuth.getInstance()

    private val users = FirebaseFirestore
        .getInstance()
        .collection("users")

    override suspend fun register(user: User) {
        val result = auth
            .createUserWithEmailAndPassword(user.email, user.password)
            .await()

        val uid = result.user?.uid
            ?: throw IllegalStateException("Failed to register user")

        val profile = mapOf(
            "name" to user.name,
            "surname" to user.surname,
            "email" to user.email
            // add other User fields (e.g. name, surname) if you have them
        )
        users.document(uid).set(profile).await()
    }

    override suspend fun login(email: String, password: String): User? {
        return try {
            val result = auth
                .signInWithEmailAndPassword(email, password)
                .await()

            val uid = result.user?.uid ?: return null

            val snap = users.document(uid).get().await()
            return snap.takeIf { it.exists() }?.toUser()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getUserByEmail(email: String): User? {
        val snap = users
            .whereEqualTo("email", email)
            .limit(1)
            .get()
            .await()

        return snap.documents.firstOrNull()?.toUser()
    }

    override suspend fun getAllUsers(): List<User> {
        return users.get().await()
            .documents
            .map { it.toUser() }
    }

    override suspend fun getUserById(id: Int): User? {
        return users.get().await()
            .documents
            .map { it.toUser() }
            .firstOrNull { it.id == id }
    }

    // —— Mapping helpers ——

    private fun DocumentSnapshot.toUser(): User {
        val data = data ?: emptyMap<String, Any>()

        return User(
            id       = id.hashCode(),
            name = data["name"] as? String ?: "",
            surname  = data["surname"] as? String ?: "",
            email    = data["email"]    as? String ?: "",
            password = ""
        )
    }
}