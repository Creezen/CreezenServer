package com.jayce.vexis.business.controllers

import com.creezen.commontool.bean.PeerAdviceBean
import com.jayce.vexis.core.MyDispatchServlet
import com.jayce.vexis.business.dao.SeniorDao
import com.jayce.vexis.foundation.Log
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class SeniorManager: MyDispatchServlet() {

    private val log by lazy { Log(this::class.java) }

    @Autowired
    lateinit var seniorDao: SeniorDao

    @RequestMapping("/postAdvice")
    @ResponseBody
    fun postAdvice(peerAdviceBean: PeerAdviceBean): Boolean {
        log.d("receive:  $peerAdviceBean")
        seniorDao.addAdvice(peerAdviceBean)
        return true
    }

    @RequestMapping("/getAdvice")
    @ResponseBody
    fun getAdvice(primary: String, second: String, tertiary: String): List<PeerAdviceBean> {
        val query = PeerAdviceBean(primary, second, tertiary, "")
        val list = seniorDao.getAdvice(query)
        return list
    }
}