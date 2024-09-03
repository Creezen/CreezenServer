package cn.LJW.Entities.User

interface UserDao {

    fun findByID(userID: String): User
    fun findByName(name: String): User

    fun registerUser(user: User)
    fun registerActiveData(userID: String)

    fun getAllUser(): List<ActiveData>

    fun setAdmin(userId: String): Boolean
    fun deleteUser(userId: String): Boolean
}