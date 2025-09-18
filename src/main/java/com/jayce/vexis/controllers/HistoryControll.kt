package com.jayce.vexis.controllers

import com.creezen.commontool.bean.HistoryBean
import com.jayce.vexis.MyDispatchServlet
import com.jayce.vexis.dao.HistoryDao
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class HistoryControll: MyDispatchServlet() {

    private val mapper by lazy {
        sqlSessionFactory.openSession(true)?.getMapper(HistoryDao::class.java)
    }

    @RequestMapping("/sendEvent")
    @ResponseBody
    fun sendEvent(time: String, event: String): Boolean {
        mapper?.insertEvent(HistoryBean(time, event))
        return true
    }

    @RequestMapping("/queryAllEvent")
    @ResponseBody
    fun queryAllEvent(): List<HistoryBean> {
        val list = mapper?.queryAllEvent() ?: return listOf()
        return list
    }
}