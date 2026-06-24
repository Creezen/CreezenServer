package com.jayce.vexis.business.dao

import com.jayce.vexis.util.bean.FeedbackBean

interface FeedbackDao {

    fun insertFeedback(feedbackBean: FeedbackBean)

    fun getFeedback(): List<FeedbackBean>

    fun supportFeedback(feedbackId: String, count: Int)
}