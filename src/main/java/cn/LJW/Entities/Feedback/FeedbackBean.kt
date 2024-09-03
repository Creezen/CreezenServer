package cn.LJW.Entities.Feedback

data class FeedbackBean(
    val feedbackID: String,
    val userName: String,
    val userID: String,
    val type: String,
    val title: String,
    val content: String,
    val createTime: String,
    val support: Long,
    val against: Long
)