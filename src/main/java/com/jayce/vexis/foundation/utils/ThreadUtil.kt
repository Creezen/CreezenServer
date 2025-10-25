package com.jayce.vexis.foundation.utils

object ThreadUtil {

    fun workInThread(func: () -> Unit) {
        Thread {
            kotlin.runCatching {
                func.invoke()
            }.onFailure {
                it.printStackTrace()
            }
        }.start()
    }

    fun workInThreadBlocked(func: () -> Boolean) {
        workInThread {
            while (true) {
                if(func.invoke().not()) {
                    break
                }
            }
        }
    }
}