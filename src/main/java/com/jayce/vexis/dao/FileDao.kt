package com.jayce.vexis.dao

import com.creezen.commontool.bean.FileBean

interface FileDao {

    fun insertFile(file: FileBean)

    fun getFile(): List<FileBean>

    fun findFileByHash(fileHash: String): FileBean?
}