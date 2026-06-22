package com.jayce.vexis.util.bean

data class ActiveBean(
    val userID: String,
    val nickname: String? = null,
    val createTime: String,
    val level: Int = 0,
    val adminLevel: Int = 0,
    val support: Long = 0,
    val against: Long = 0,
    val inform: Int = 0,
    val reported: Int = 0,
    val follow: Long = 0,
    val fans: Long = 0,
    val post: Int = 0,
){
    fun isAdministrator(): Boolean {
        return adminLevel > 0
    }
}
