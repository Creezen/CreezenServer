package com.jayce.vexis.business.controllers

import com.creezen.commontool.bean.FeedbackBean
import com.jayce.vexis.business.dao.FeedbckDao
import com.jayce.vexis.core.MyDispatchServlet
import org.json.JSONObject
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class FeedbackManager: MyDispatchServlet() {

    private val mapper by lazy {
        sqlSessionFactory?.openSession(true)?.getMapper(FeedbckDao::class.java)
    }

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
        mapper?.insertFeedback(feedbackBean)
        println("$userID  $title  $content  $feedbackBean")
        return true
    }

    @RequestMapping("/getFeedback")
    @ResponseBody
    fun getFeedback(): String {
        return JSONObject().apply {
            put("items", mapper?.getFeedback())
        }.toString()
    }
}