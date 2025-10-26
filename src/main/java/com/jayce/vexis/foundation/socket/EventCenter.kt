package com.jayce.vexis.foundation.socket

import com.jayce.vexis.foundation.utils.ThreadUtil.workInThreadBlocked
import org.springframework.stereotype.Component
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap

@Component
class EventCenter(val serverSocket: ServerSocket) {

    private var socketMap: ConcurrentHashMap<String, UserSocket> = ConcurrentHashMap()

    fun start() {
        workInThreadBlocked {
            println("wait for socket")
            val socket = serverSocket.accept()
            operation(socket)
            return@workInThreadBlocked true
        }
    }

    private fun operation(socket: Socket) {
        val newSocket = UserSocket(socket) { sock, id ->
            val cacheSocket = socketMap[id]
            if (cacheSocket != null) {
                if (!cacheSocket.isDied) {
                    cacheSocket.destroy()
                    socketMap.remove(id)
                    socketMap[id] = sock
                } else {
                    socketMap.remove(id)
                }
            }
        }
        newSocket.init()
    }
}