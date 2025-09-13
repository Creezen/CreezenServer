package com.jayce.vexis.entities.user

data class ActiveData (
    var userID: String? = null,
    var nickname: String? = null,
    var createTime: String? = null,
    var support: Long = 0,
    var against: Long = 0,
    var inform: Long = 0,
    var reported: Long = 0,
    var follow: Long = 0,
    var fans: Long = 0,
    var post: Long = 0
)