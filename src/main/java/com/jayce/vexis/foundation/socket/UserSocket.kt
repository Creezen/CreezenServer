package com.jayce.vexis.foundation.socket

import com.jayce.vexis.foundation.utils.RedisUtil.STREAM_CONTENT_KEY
import com.jayce.vexis.foundation.utils.RedisUtil.STREAM_MESSAGE_ID
import com.jayce.vexis.foundation.utils.RedisUtil.ack
import com.jayce.vexis.foundation.utils.RedisUtil.readStream
import com.jayce.vexis.foundation.utils.RedisUtil.writeStream
import com.jayce.vexis.foundation.utils.ThreadUtil.workInThreadBlocked
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket

class UserSocket(private val socket: Socket) {

    private lateinit var reader: BufferedReader
    private lateinit var writer: BufferedWriter

    fun init() {
        reader = BufferedReader(InputStreamReader(socket.getInputStream(), "UTF-8"))
        writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream(), "UTF-8"))
        start()
    }

    private fun start() {
        writeContent()
        readMessage()
    }

    private fun writeContent() {
        workInThreadBlocked {
            val line = reader.readLine()
            println("read: $line")
            if (line.isNullOrEmpty()) {
                destroy()
                return@workInThreadBlocked false
            }
            writeStream(line)
            return@workInThreadBlocked true
        }
    }

    private fun readMessage() {
        workInThreadBlocked {
            readStream<String, String>().forEach {
                val json = JSONObject(it.value[STREAM_CONTENT_KEY])
                json.put(STREAM_MESSAGE_ID, it.id)
                val content =  json.toString()
                println("write: $content")
                writer.write("$content\n")
                writer.flush()
                ack(it.id)
            }
            return@workInThreadBlocked true
        }
    }

    fun destroy() {
        kotlin.runCatching {
            reader.close()
            writer.close()
            socket.close()
        }.onFailure {
            it.printStackTrace()
        }
    }
}