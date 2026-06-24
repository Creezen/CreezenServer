package com.jayce.vexis.util.bean

import java.math.BigInteger

data class RemarkBean(
    val sectionId: Long,
    val userId: String,
    val remarkId: Long,
    val content: String,
    val type: Int,
    val favor: Long,
    val createTime: Long
)
