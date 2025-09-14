package com.jayce.vexis.entities.file

import com.creezen.commontool.FileBean

interface FileDao {

    fun insertFile(file: FileBean)

    fun getFile(): List<FileBean>

    fun findFileByHash(fileHash: String): FileBean?
}