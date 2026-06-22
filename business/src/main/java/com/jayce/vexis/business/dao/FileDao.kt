package com.jayce.vexis.business.dao

import com.jayce.vexis.util.bean.FileBean

interface FileDao {

    fun insertFile(file: FileBean)

    fun getFile(): List<FileBean>

    fun findFileByHash(fileHash: String): FileBean?
}