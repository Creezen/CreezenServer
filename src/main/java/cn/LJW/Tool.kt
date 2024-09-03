package cn.LJW

object Tool {

    fun workInThread(func: () -> Unit) {
        Thread {
            kotlin.runCatching {
                func.invoke()
            }.onFailure {
                println("run catch error: $it")
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