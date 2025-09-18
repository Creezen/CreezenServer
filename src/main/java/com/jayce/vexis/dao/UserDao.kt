package com.jayce.vexis.dao

import com.creezen.commontool.bean.ActiveBean
import com.creezen.commontool.bean.UserBean

interface UserDao {

    fun findByID(userID: String): UserBean?
    fun findByName(name: String): UserBean?

    fun registerUser(user: UserBean)
    fun registerActiveData(userID: String)

    fun getAllUser(): List<ActiveBean>

    fun setAdmin(userId: String): Boolean
    fun deleteUser(userId: String): Boolean
}