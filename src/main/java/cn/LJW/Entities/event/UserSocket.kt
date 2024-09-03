package cn.LJW.Entities.event

import cn.LJW.Tool.workInThreadBlocked
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

class UserSocket(socket: Socket) {

    private val reader: BufferedReader
    private val writer: BufferedWriter

    init {
        reader = BufferedReader(InputStreamReader(socket.getInputStream(), "UTF-8"))
        writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream(), "UTF-8"))
        workInThreadBlocked {
            val line = reader.readLine()
            println("get line: $line")
            if (line.isNullOrEmpty()) {
                return@workInThreadBlocked false
            }
            writer.write(line+"\n")
            writer.flush()
            return@workInThreadBlocked true
        }
    }

}