package com.jayce.vexis.foundation.socket

import com.creezen.commontool.Config
import com.creezen.commontool.Config.EventType.EVENT_TYPE_FINISH
import com.creezen.commontool.bean.TelecomBean
import com.creezen.commontool.toBean
import com.jayce.vexis.foundation.utils.RedisUtil
import com.jayce.vexis.foundation.utils.RedisUtil.STREAM_CONTENT_KEY
import com.jayce.vexis.foundation.utils.RedisUtil.STREAM_MESSAGE_ID
import com.jayce.vexis.foundation.utils.RedisUtil.ack
import com.jayce.vexis.foundation.utils.RedisUtil.readStream
import com.jayce.vexis.foundation.utils.RedisUtil.sendFinishMsg
import com.jayce.vexis.foundation.utils.RedisUtil.setOfflineStatus
import com.jayce.vexis.foundation.utils.RedisUtil.verifyOnlineStatus
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
    private val deferred = CompletableDeferred<String>()

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
                println("接收消息： $line")
                if (line.isNullOrEmpty()) {
                    sendFinishMsg(deferred.await())
                    setOfflineStatus(deferred.await())
                    destroy()
                    continue
                }
                if (!identify(line)) return@launch
                writeStream(line)
            }
        }
    }

    private suspend fun identify(line: String): Boolean {
        val msg = line.toBean<TelecomBean>() ?: return false
        if (msg.type == Config.EventType.EVENT_TYPE_DEFAULT) {
            RedisUtil.createStreamGroupIfNeed(msg.content)
            deferred.complete(msg.content)
            if (verifyOnlineStatus(msg)) {
                callback.invoke(this@UserSocket, msg.content)
            } else {
                sendFinishMsg(deferred.await(), msg)
                return false
            }
        }
        return true
    }

    private fun readMessage() {
        scope.launch {
            while (true) {
                val currentUserId = deferred.await()
                readStream<String, String>(currentUserId, isFirst).forEach {
                    println("发送消息： ${it.value}")
                    val json = JSONObject(it.value[STREAM_CONTENT_KEY])
                    val type = json.optInt("type", -1)
                    val userId = json.optString("userId", "")
                    val session = json.optString("session", "")
                    if (type == EVENT_TYPE_FINISH) {
                        ack(currentUserId, it.id)
                        if (userId == currentUserId && session.isNotEmpty()) {
                            setOfflineStatus(currentUserId)
                            val content =  json.toString()
                            write(content)
                            destroy()
                            return@launch
                        } else {
                            return@forEach
                        }
                    }
                    json.put(STREAM_MESSAGE_ID, it.id)
                    val content =  json.toString()
                    write(content)
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

    private fun write(content: String) {
        kotlin.runCatching {
            writer.write("$content\n")
            writer.flush()
        }.onFailure {
            it.printStackTrace()
        }
    }
}