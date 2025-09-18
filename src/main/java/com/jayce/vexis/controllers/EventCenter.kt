package com.jayce.vexis.controllers

import com.jayce.vexis.MyDispatchServlet
import com.jayce.vexis.Tool.workInThread
import com.jayce.vexis.UserSocket
import org.springframework.stereotype.Component
import java.net.ServerSocket
import java.net.Socket

@Component
class EventCenter(val serverSocket: ServerSocket): MyDispatchServlet() {

    private var isInit: Boolean = false

    fun beginSocket() {
        if (isInit) return
        isInit = true
        workInThread {
            while (true) {
                println("wait for socket")
                val socket = serverSocket.accept()
                initSocket(socket)
            }
        }
    }

    private fun initSocket(socket: Socket) {
        UserSocket(socket)
    }

}