package com.jayce.vexis.entities.article

import java.math.BigInteger

data class CommentBean(
    val synergyId: BigInteger,
    val paragraphId: Long,
    val userId: String,
    val commentId: Long,
    val cotent: String,
    val favor: Long,
    val createTime: BigInteger
)
