package com.jayce.vexis.util.bean

import com.jayce.vexis.util.Config.NIL

data class UserBean(
    val userId: String = "-1",
    val nickname: String = "NickUser",
    val name: String = "匿名用户",
    val age: Int = -1,
    val sex: String = "Unknow",
    val password: String = NIL,
    val createTime: String = NIL,
    val count: Long = -1,
    val level: Int = 0,
    val adminLevel: Int = 0,
    val isEdit: Int = -1,
    val email: String = NIL,
    val selfIntroduction: String = NIL,
    val phone: String = NIL,
    val address: String = NIL,
    val birthday: String = NIL,
    val headType: String = NIL,
    val session: String = NIL
) {
    fun isAdministrator(): Boolean {
        return adminLevel > 0
    }
}
