package com.jayce.vexis.dao

import com.creezen.commontool.bean.FeedbackBean

interface FeedbckDao {

    fun insertFeedback(feedbackBean: FeedbackBean)

    fun getFeedback(): List<FeedbackBean>
}