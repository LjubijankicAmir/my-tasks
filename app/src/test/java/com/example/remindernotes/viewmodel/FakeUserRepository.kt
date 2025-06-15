import com.example.remindernotes.data.User
import com.example.remindernotes.repository.IUserRepository

class FakeUserRepository : IUserRepository {
    private val users = mutableListOf<User>()

    override suspend fun register(user: User) {
        users.add(user)
    }

    override suspend fun login(email: String, password: String): User? {
        return users.find { it.email == email && it.password == password }
    }

    override suspend fun getUserByEmail(email: String): User? {
        return users.find { it.email == email }
    }

    override suspend fun getAllUsers(): List<User> = users

    override suspend fun getUserById(id: Int): User? = users.find { it.id == id }
}
