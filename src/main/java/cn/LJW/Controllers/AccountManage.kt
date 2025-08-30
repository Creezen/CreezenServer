package cn.LJW.Controllers

import cn.LJW.Entities.Net.OnlineSession
import cn.LJW.Entities.User.User
import cn.LJW.Entities.User.UserDao
import cn.LJW.MyDispatchServlet
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpSession
import org.json.JSONObject
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile
import java.io.File

@Controller
class AccountManage : MyDispatchServlet() {

    companion object {
        private val sessionMap: MutableMap<String, OnlineSession> = HashMap()
    }

    @RequestMapping(value = ["/login"])
    @ResponseBody
    fun login(
        @CookieValue("firstTime") firstTimeCookie: String,
        @CookieValue("lastTime") lastTimeCookie: String?,
        session: HttpSession,
        response: HttpServletResponse,
        unique: String,
        password: String
    ): String? {
        println("$unique   $password")
        val sqlSession = sqlSessionFactory?.openSession(true) ?: return null
        val userDao = sqlSession.getMapper(UserDao::class.java)
        val user = (if (unique.length < 20) userDao.findByName(unique) else userDao.findByID(unique))
            ?: return getIntJSONString(0)
        if (user.password != password) return getIntJSONString(1)
        if (!sessionMap.containsKey(unique)) {
            val onlineSession = OnlineSession(firstTimeCookie, session.id)
            onlineSession.loginTime.add(lastTimeCookie)
            sessionMap[unique] = onlineSession
        } else if (firstTimeCookie == sessionMap[unique]!!.firstTimeCookie) {
            sessionMap[unique]!!.loginTime.add(lastTimeCookie)
            session.setAttribute(lastTimeCookie, password)
        } else {
            response.addCookie(Cookie("JSESSIONID", sessionMap[unique]!!.sessionID))
            sessionMap[unique]!!.firstTimeCookie = firstTimeCookie
        }
        return getJSONString(user)
    }

    @RequestMapping(value = ["/register"])
    @ResponseBody
    fun register(type: String, requestUser: User): String {
        val session = sqlSessionFactory?.openSession(true) ?: return ""
        val userDao = session.getMapper(UserDao::class.java)
        return if (type == "0") {
            val user = userDao.findByName(requestUser.name)
            if (user == null) getIntJSONString(0) else getIntJSONString(1)
        } else {
            userDao.registerUser(requestUser)
            userDao.registerActiveData(requestUser.userId)
            getIntJSONString(2)
        }
    }

    @RequestMapping(value = ["/postAvatar"])
    @ResponseBody
    fun uplaodAvatar(userID: String, file: MultipartFile): String {
        println("$userID  ${file.originalFilename}")
        file.transferTo(File("D:/FileSystem/head/$userID.png"))
        return JSONObject().run {
            put("status", true)
            toString()
        }
    }

    @RequestMapping(value = ["/getAllUser"])
    @ResponseBody
    fun getAllUsers(): String{
        val session = sqlSessionFactory?.openSession(true) ?: return ""
        session.use {
            val mapper = it.getMapper(UserDao::class.java)
            val result = mapper.getAllUser()
            return JSONObject().run {
                put("userActiveData", result)
                toString()
            }
        }
    }

    @RequestMapping(value = ["/managerUser"])
    @ResponseBody
    fun manageUser(operation: Int, userId: String): String {
        val session = sqlSessionFactory?.openSession(true)
            ?: return JSONObject().run {
                put("operationResult",  false)
                toString()
            }
        session.use {
            val mapper = it.getMapper(UserDao::class.java)
            var result = true
            kotlin.runCatching {
                if (operation == 1){
                    mapper.setAdmin(userId)
                } else {
                    mapper.deleteUser(userId)
                }
            }.onFailure {
                println(it)
                result = false
            }
            return JSONObject().run {
                put("operationResult", result)
                toString()
            }
        }
    }

    private fun getJSONString(user: User): String {
        return JSONObject(user).toString()
    }

    private fun getIntJSONString(flag: Int): String {
        return JSONObject().put("status", flag).toString()
    }
}