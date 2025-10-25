package com.jayce.vexis.business.controllers

import com.creezen.commontool.bean.PeerAdviceBean
import com.jayce.vexis.core.MyDispatchServlet
import com.jayce.vexis.business.dao.SeniorDao
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class SeniorManager: MyDispatchServlet() {

    private val mapper by lazy {
        val session = sqlSessionFactory.openSession(true)
        session.getMapper(SeniorDao::class.java)
    }

    @RequestMapping("/postAdvice")
    @ResponseBody
    fun postAdvice(peerAdviceBean: PeerAdviceBean): Boolean {
        println("receive:  $peerAdviceBean")
        mapper.addAdvice(peerAdviceBean)
        return true
    }

    @RequestMapping("/getAdvice")
    @ResponseBody
    fun getAdvice(primary: String, second: String, tertiary: String): List<PeerAdviceBean> {
        val query = PeerAdviceBean(primary, second, tertiary, "")
        val list = mapper.getAdvice(query)
        return list
    }
}