package com.jayce.vexis.util.bean

data class FeedbackBean(
    val feedbackID: String,
    val userName: String,
    val userID: String,
    val type: String,
    val title: String,
    val content: String,
    val createTime: Long,
    val support: Long,
    val against: Long
)
