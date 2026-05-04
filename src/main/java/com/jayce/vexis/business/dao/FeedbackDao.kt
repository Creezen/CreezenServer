package com.jayce.vexis.business.dao

import com.creezen.commontool.bean.FeedbackBean

interface FeedbackDao {

    fun insertFeedback(feedbackBean: FeedbackBean)

    fun getFeedback(): List<FeedbackBean>
}