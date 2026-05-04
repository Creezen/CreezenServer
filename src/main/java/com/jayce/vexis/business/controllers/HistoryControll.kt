package com.jayce.vexis.business.controllers

import com.creezen.commontool.bean.HistoryBean
import com.jayce.vexis.core.MyDispatchServlet
import com.jayce.vexis.business.dao.HistoryDao
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class HistoryControll: MyDispatchServlet() {

    @Autowired
    lateinit var historyDao: HistoryDao

    @RequestMapping("/sendEvent")
    @ResponseBody
    fun sendEvent(time: String, event: String): Boolean {
        historyDao.insertEvent(HistoryBean(time, event))
        return true
    }

    @RequestMapping("/queryAllEvent")
    @ResponseBody
    fun queryAllEvent(): List<HistoryBean> {
        val list = historyDao.queryAllEvent()
        return list
    }
}