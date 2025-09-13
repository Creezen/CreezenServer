package com.jayce.vexis.entities.file

interface FileDao {

    fun insertFile(file: FileBean)

    fun getFile(): List<FileBean>

    fun findFileByHash(fileHash: String): FileBean?
}