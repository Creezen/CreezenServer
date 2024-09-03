package cn.LJW.Controllers

import cn.LJW.Entities.event.UserSocket
import cn.LJW.MyDispatchServlet
import cn.LJW.Tool.workInThread
import com.creezen.commontool.CreezenTool.getRandomString
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