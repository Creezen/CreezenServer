package com.jayce.vexis.util.bean

import com.jayce.vexis.util.Config.NIL

data class TransferStatusBean(
    val statusCode: Int,
    val data: String = NIL
)
