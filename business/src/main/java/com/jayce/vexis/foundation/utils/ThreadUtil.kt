package com.jayce.vexis.foundation.utils

import com.jayce.vexis.foundation.Log

object ThreadUtil {

    private val log by lazy { Log(this::class.java) }

    fun workInThread(func: () -> Unit) {
        Thread {
            kotlin.runCatching {
                func.invoke()
            }.onFailure {
                log.d("error: ${it.message}")
            }
        }.start()
    }

    fun workInThreadBlocked(func: () -> Boolean) {
        workInThread {
            while (func.invoke()){}
        }
    }
}