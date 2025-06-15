import com.example.remindernotes.data.IUserPreferences

class FakeUserPreferences : IUserPreferences  {
    private var loggedIn = false
    private var userId: Int = -1

    override fun isUserLoggedIn(): Boolean = loggedIn
    override fun setUserLoggedIn(isLoggedIn: Boolean) {
        loggedIn = isLoggedIn
    }

    override fun setLoggedInUserId(id: Int) {
        userId = id
    }

    override fun getLoggedInUserId(): Int = userId
    override fun clear() {
        loggedIn = false
        userId = -1
    }
}
