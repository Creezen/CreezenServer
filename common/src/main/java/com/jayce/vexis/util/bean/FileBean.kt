package com.jayce.vexis.util.bean

import com.jayce.vexis.util.Config.NIL

data class FileBean(
    val userId: String = NIL,
    val fileName: String = NIL,
    val fileID: String = NIL,
    val fileSuffix: String = NIL,
    val description: String = NIL,
    val illustrate: String = NIL,
    val fileSize: Long = 0,
    val uploadTime: String = NIL,
    var fileHash: String = NIL
)