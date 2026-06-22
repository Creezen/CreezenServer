package com.jayce.vexis.business.dao

import com.jayce.vexis.util.bean.ActiveBean
import com.jayce.vexis.util.bean.UserBean

interface UserDao {

    fun findByID(userID: String): UserBean?
    fun findByName(name: String): UserBean?

    fun registerUser(user: UserBean)
    fun registerActiveData(userID: String)

    fun getAllUser(): List<ActiveBean>

    fun setAdmin(userId: String): Boolean
    fun deleteUser(userId: String): Boolean
}