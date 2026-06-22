package com.jayce.vexis.util.bean

import com.jayce.vexis.util.Config.NIL

data class TelecomBean (
    val type: Int,
    val userId: String,
    val nickName: String,
    val session: String,
    val time: Long,
    val msgId: String = "-1",
    val content: String = NIL
)
