package com.jayce.vexis.business.controllers

import com.creezen.commontool.bean.ActiveBean
import com.creezen.commontool.bean.TransferStatusBean
import com.creezen.commontool.bean.UserBean
import com.creezen.commontool.toJson
import com.jayce.vexis.business.dao.UserDao
import com.jayce.vexis.core.MyDispatchServlet
import com.jayce.vexis.foundation.Log
import com.jayce.vexis.foundation.utils.RedisUtil.isUserAlreadyOnline
import com.jayce.vexis.foundation.utils.RedisUtil.setOnlineStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.*

@Controller
class AccountManage : MyDispatchServlet() {

    private val log by lazy { Log(this::class.java) }

    @Autowired
    lateinit var userDao: UserDao

    @RequestMapping(value = ["/login"])
    @ResponseBody
    fun login(unique: String, password: String): TransferStatusBean {
        val user = (if (unique.length < 20) userDao.findByName(unique) else userDao.findByID(unique))
            ?: return status(0)
        if (user.password != password) return status(1)
        if (isUserAlreadyOnline(user.userId)) {
            return status(-3)
        }
        val session = UUID.randomUUID().toString()
        setOnlineStatus(user.userId, session)
        val userJson = user.copy(session = session).toJson()
        return status(-1, userJson)
    }

    @RequestMapping(value = ["/register"])
    @ResponseBody
    fun register(@RequestBody requestUser: UserBean): TransferStatusBean {
        userDao.registerUser(requestUser)
        userDao.registerActiveData(requestUser.userId)
        return status(2)
    }

    @RequestMapping(value = ["/checkInfo"])
    @ResponseBody
    fun checkInfo(userName: String): Boolean {
        val user = userDao.findByName(userName)
        return (user == null)
    }

    @RequestMapping(value = ["/postAvatar"])
    @ResponseBody
    fun uploadAvatar(userID: String, file: MultipartFile): Boolean {
        //根据web.xml里面的file-size-threshold判断是否要存在磁盘（文件夹下）
        //MultipartFile操作的实际上是临时文件夹下面的文件
        //在请求结束后，这个MultipartFile实例被销毁，临时文件夹被删除
        log.d("$userID  ${file.originalFilename}")
//        val hash = FileHelper.getFileHash(file.inputStream, "SHA256")
        file.transferTo(File("D:/FileSystem/head/$userID.png"))
        return true
    }

    @RequestMapping(value = ["/getAllUser"])
    @ResponseBody
    fun getAllUsers(): List<ActiveBean> {
        return userDao.getAllUser()
    }

    @RequestMapping(value = ["/managerUser"])
    @ResponseBody
    fun manageUser(operation: Int, userId: String): Boolean {
        var result = true
        kotlin.runCatching {
            if (operation == 1){
                userDao.setAdmin(userId)
            } else {
                userDao.deleteUser(userId)
            }
        }.onFailure {
            it.printStackTrace()
            result = false
        }
        return result
    }

    private fun status(code: Int, data: String? = ""): TransferStatusBean {
        val value = data ?: ""
        return TransferStatusBean(code, value)
    }
}