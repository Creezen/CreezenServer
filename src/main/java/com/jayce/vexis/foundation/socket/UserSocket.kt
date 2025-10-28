package com.jayce.vexis.foundation.socket

import com.creezen.commontool.Config
import com.creezen.commontool.bean.TelecomBean
import com.creezen.commontool.toBean
import com.jayce.vexis.foundation.utils.RedisUtil
import com.jayce.vexis.foundation.utils.RedisUtil.STREAM_CONTENT_FINISH
import com.jayce.vexis.foundation.utils.RedisUtil.STREAM_CONTENT_KEY
import com.jayce.vexis.foundation.utils.RedisUtil.STREAM_MESSAGE_ID
import com.jayce.vexis.foundation.utils.RedisUtil.ack
import com.jayce.vexis.foundation.utils.RedisUtil.readStream
import com.jayce.vexis.foundation.utils.RedisUtil.sendFinishMsg
import com.jayce.vexis.foundation.utils.RedisUtil.writeStream
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean

class UserSocket(private val socket: Socket, private val callback: (UserSocket, String) -> Unit) {

    private lateinit var reader: BufferedReader
    private lateinit var writer: BufferedWriter

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var userId: String? = null
    var isDied: Boolean = false
    var isFirst: AtomicBoolean = AtomicBoolean(true)

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
        scope.launch {
            while (true) {
                val line = reader.readLine()
                if (line.isNullOrEmpty()) {
                    sendFinishMsg()
                    destroy()
                    continue
                }
                identify(line)
                writeStream(line)
            }
        }
    }

    private fun identify(line: String) {
        if (userId != null) return
        val msg = line.toBean<TelecomBean>()
        if (msg != null && msg.type == Config.EventType.EVENT_TYPE_DEFAULT) {
            RedisUtil.createStreamGroupIfNeed(msg.content)
            userId = msg.content
            callback.invoke(this@UserSocket, msg.content)
        }
    }

    private fun readMessage() {
        scope.launch {
            while (true) {
                val currentUserId = userId
                if (currentUserId == null) {
                    delay(200)
                    continue
                }
                readStream<String, String>(currentUserId, isFirst).forEach {
                    val finishMsg = it.value[STREAM_CONTENT_FINISH]
                    if (finishMsg != null) {
                        ack(currentUserId, it.id)
                        destroy()
                        return@launch
                    }
                    val json = JSONObject(it.value[STREAM_CONTENT_KEY])
                    json.put(STREAM_MESSAGE_ID, it.id)
                    val content =  json.toString()
                    writer.write("$content\n")
                    writer.flush()
                    ack(currentUserId, it.id)
                }
            }
        }
    }

    fun destroy() {
        if (isDied) return
        isDied = true
        kotlin.runCatching {
            reader.close()
            writer.close()
            socket.close()
        }.onFailure {
            it.printStackTrace()
        }

        kotlin.runCatching {
            scope.cancel()
        }.onFailure {
            it.printStackTrace()
        }
    }
}