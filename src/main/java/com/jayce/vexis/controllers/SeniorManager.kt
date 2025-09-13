package com.jayce.vexis.controllers

import com.jayce.vexis.entities.senior.SeniorAdvice
import com.jayce.vexis.entities.senior.SeniorDao
import com.jayce.vexis.MyDispatchServlet
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
    fun postAdvice(seniorAdvice: SeniorAdvice): Boolean {
        println("receive:  $seniorAdvice")
        mapper.addAdvice(seniorAdvice)
        return true
    }

    @RequestMapping("/getAdvice")
    @ResponseBody
    fun getAdvice(primary: String, second: String, tertiary: String): List<SeniorAdvice> {
        val query = SeniorAdvice(primary, second, tertiary, )
        val list = mapper.getAdvice(query)
        return list
    }
}