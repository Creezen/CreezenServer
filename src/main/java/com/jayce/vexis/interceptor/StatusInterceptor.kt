package com.jayce.vexis.interceptor

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.servlet.HandlerInterceptor

class StatusInterceptor: HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
//        println("begin to preHandle")
//        val opsForStream = MyDispatchServlet.redisTemplate?.opsForStream<String, String>() ?: return false
//        val result = opsForStream.read(
//            StreamReadOptions.empty().block(Duration.ofSeconds(30)),
//            StreamOffset.create("ljw", ReadOffset.from("0-0"))
//        )
//        result.forEach {
//            val y = it.value
//            val x = it.stream
//            val z = it.id
//            println("x  y  z:  $x    $y   $z")
//            it.forEach {
//                val a = it.key
//                val b = it.value
//                println("a  b:  $a    $b")
//            }
//        }
//        x  y  z:  ljw    {age=666, name=lzw}   1722148735605-0
//        a  b:  age    666
//        a  b:  name    lzw
//        x  y  z:  ljw    {age=666, name=lzw}   1722149334249-0
//        a  b:  age    666
//        a  b:  name    lzw
        return true

//        val myData: MutableMap<String, String> = mutableMapOf()
//        myData["field1"] = "value1"
//        myData["field2"] = "value2"
//        opsForStream.add("fff", myData)
//        return true
    }

}