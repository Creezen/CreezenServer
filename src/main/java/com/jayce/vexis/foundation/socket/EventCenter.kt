package com.jayce.vexis.foundation.socket

import com.jayce.vexis.foundation.utils.ThreadUtil.workInThread
import org.springframework.stereotype.Component
import java.net.ServerSocket
import java.net.Socket

@Component
class EventCenter(val serverSocket: ServerSocket) {

    private var eventSocket: UserSocket? = null

    fun start() {
        workInThread {
            while (true) {
                println("wait for socket")
                val socket = serverSocket.accept()
                operation(socket)
            }
        }
    }

    private fun operation(socket: Socket) {
        eventSocket?.destroy()
        eventSocket = null
        eventSocket = UserSocket(socket)
        eventSocket?.init()
    }

}