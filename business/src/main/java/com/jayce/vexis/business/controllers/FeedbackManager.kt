package com.jayce.vexis.business.controllers

import com.alibaba.druid.sql.visitor.functions.If
import com.jayce.vexis.util.bean.FeedbackBean
import com.jayce.vexis.business.dao.FeedbackDao
import com.jayce.vexis.business.dao.UserDao
import com.jayce.vexis.core.MyDispatchServlet
import com.jayce.vexis.foundation.Log
import com.jayce.vexis.foundation.utils.RedisUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class FeedbackManager: MyDispatchServlet() {

    private val log by lazy { Log(this::class.java) }

    @Autowired
    lateinit var feedbackDao: FeedbackDao

    @Autowired
    lateinit var userDao: UserDao

    @RequestMapping("/sendFeedback")
    @ResponseBody
    fun sendFeedback(
        feedbackID: String,
        userID: String,
        title: String,
        content: String,
        type: String
    ): Boolean {
        val userName = userDao.findByID(userID)?.name ?: "匿名用户"
        val feedbackBean = FeedbackBean(
            feedbackID,
            userName,
            userID,
            type,
            title,
            content,
            System.currentTimeMillis(),
            0,
            0
        )
        feedbackDao.insertFeedback(feedbackBean)
        return true
    }

    @RequestMapping("/getFeedback")
    @ResponseBody
    fun getFeedback(): List<FeedbackBean> {
        return feedbackDao.getFeedback()
    }

    @RequestMapping("/supportFeedback")
    @ResponseBody
    fun supportFeedback(userId: String, feedbackId: String): Boolean {
        val status = RedisUtil.removeSupportFeedback(userId, feedbackId)
        if (status) {
            feedbackDao.supportFeedback(feedbackId, -1)
            return false
        } else {
            RedisUtil.feedbackSupport(userId, feedbackId)
            feedbackDao.supportFeedback(feedbackId, 1)
            return true
        }
    }
}