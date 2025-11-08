package com.jayce.vexis.core

import com.creezen.commontool.Config.NetworkParam.COOKIE_USER_ID
import com.creezen.commontool.Config.NetworkParam.COOKIE_UUID
import com.creezen.commontool.bean.TransferStatusBean
import com.creezen.commontool.toJson
import com.jayce.vexis.foundation.utils.RedisUtil.verifyOnlineStatus
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.servlet.HandlerInterceptor

class StatusInterceptor: HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        kotlin.runCatching {
            var userId: String? = null
            var session: String? = null
            request.cookies.forEach {
                if (it.name.equals(COOKIE_USER_ID)) {
                    userId = it.value
                }
                if (it.name.equals(COOKIE_UUID)) {
                    session = it.value
                }
            }
            if (userId == null) return false
            if (session == null) return false
            if (verifyOnlineStatus(userId, session)) {
                return true
            }
            val status = TransferStatusBean(1001, "reject")
            response.status = 200
            response.writer.write(status.toJson())
            return false
        }.onFailure {
            it.printStackTrace()
            return false
        }
        return true
    }
}