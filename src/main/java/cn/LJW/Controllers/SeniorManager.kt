package cn.LJW.Controllers

import cn.LJW.Entities.senior.SeniorAdvice
import cn.LJW.Entities.senior.SeniorDao
import cn.LJW.MyDispatchServlet
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
}