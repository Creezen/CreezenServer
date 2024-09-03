package cn.LJW.Entities.Feedback

interface FeedbckDao {

    fun insertFeedback(feedbackBean: FeedbackBean)

    fun getFeedback(): List<FeedbackBean>
}