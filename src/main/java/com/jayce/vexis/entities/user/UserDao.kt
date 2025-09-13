package com.jayce.vexis.entities.user

interface UserDao {

    fun findByID(userID: String): com.jayce.vexis.entities.user.User?
    fun findByName(name: String): com.jayce.vexis.entities.user.User?

    fun registerUser(user: com.jayce.vexis.entities.user.User)
    fun registerActiveData(userID: String)

    fun getAllUser(): List<ActiveData>

    fun setAdmin(userId: String): Boolean
    fun deleteUser(userId: String): Boolean
}