package com.jayce.vexis.business.controllers

import com.creezen.commontool.bean.FeedbackBean
import com.jayce.vexis.business.dao.FeedbackDao
import com.jayce.vexis.core.MyDispatchServlet
import com.jayce.vexis.foundation.Log
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class FeedbackManager: MyDispatchServlet() {

    private val log by lazy { Log(this::class.java) }

    @Autowired
    lateinit var feedbackDao: FeedbackDao

    @RequestMapping("/sendFeedback")
    @ResponseBody
    fun addFeedback(userID: String, title: String, content: String): Boolean {
        val feedbackBean = FeedbackBean(
            "123",
            "",
            userID,
            "normal",
            title,
            content,
            System.currentTimeMillis(),
            0,
            0
        )
        feedbackDao.insertFeedback(feedbackBean)
        log.d("$userID  $title  $content  $feedbackBean")
        return true
    }

    @RequestMapping("/getFeedback")
    @ResponseBody
    fun getFeedback(): String {
        return JSONObject().apply {
            put("items", feedbackDao.getFeedback())
        }.toString()
    }
}