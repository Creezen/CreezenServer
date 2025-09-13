package com.jayce.vexis.entities.Feedback

interface FeedbckDao {

    fun insertFeedback(feedbackBean: FeedbackBean)

    fun getFeedback(): List<FeedbackBean>
}