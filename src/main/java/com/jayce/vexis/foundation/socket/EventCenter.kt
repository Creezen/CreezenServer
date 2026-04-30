package com.jayce.vexis.foundation.socket

import com.jayce.vexis.foundation.Log
import com.jayce.vexis.foundation.utils.ThreadUtil.workInThreadBlocked
import org.springframework.stereotype.Component
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap

@Component
class EventCenter(val serverSocket: ServerSocket) {

    private val log by lazy { Log(this::class.java) }
    private var socketMap: ConcurrentHashMap<String, UserSocket> = ConcurrentHashMap()

    fun start() {
        workInThreadBlocked {
            log.d("wait for socket")
            val socket = serverSocket.accept()
            operation(socket)
            return@workInThreadBlocked true
        }
    }

    private fun operation(socket: Socket) {
        val newSocket = UserSocket(socket) { sock, id ->
            val cacheSocket = socketMap.remove(id)
            cacheSocket?.destroy()
            socketMap[id] = sock
        }
        newSocket.init()
    }
}