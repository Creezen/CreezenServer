package com.jayce.vexis.utils

import com.creezen.commontool.Util.loadDataFromYAML
import java.io.File
import java.io.InputStream
import java.security.MessageDigest

object FileHelper {

    private var _fileMap: Map<String, List<String>>? = null

    private val fileTypeMap: Map<String, List<String>>
        get() {
            return if (_fileMap != null) {
                _fileMap ?: mapOf()
            } else {
                _fileMap = loadDataFromYAML()
                _fileMap ?: mapOf()
            }
        }

    private lateinit var fileTypeList: List<String>

    private val compare = Comparator<String> { a, b ->
        val compResult = b.length - a.length
        if (compResult != 0) {
            return@Comparator compResult
        }
        a.zip(b).forEach {
            if (it.first == it.second) {
                return@forEach
            }
            return@Comparator charToCompareInt(it.first) - charToCompareInt(it.second)
        }
        return@Comparator 0
    }

    fun init() {
        if (!::fileTypeList.isInitialized) {
            fileTypeList = fileTypeMap.keys.toList().sortedWith(compare)
        }
    }

    fun getFileTypeByFileHead(fileHead: String): String {
        for (i in fileTypeList.indices) {
            if (fileHead.length < fileTypeList[i].length) {
                continue
            }
            if (fileHead.startsWith(fileTypeList[i])) {
                return fileTypeMap[fileTypeList[i]]?.first() ?: ""
            }
        }
        return ""
    }

    fun getFileHashAndHead(file: File, algorithm: String, length: Int = 10): Pair<String, String> {
        return getFileHashAndHead(file.inputStream(), algorithm, length)
    }

    fun getFileHashAndHead(stream: InputStream, algorithm: String, length: Int = 10): Pair<String, String> {
        val digest = MessageDigest.getInstance(algorithm)
        val headArray = ByteArray(length)
        var isHeadRead = false
        stream.use {
            val buffer = ByteArray(1 * 1024 * 1024)
            var contentSize: Int
            while (stream.read(buffer).also { contentSize = it } != -1) {
                if (!isHeadRead) {
                    buffer.copyInto(headArray, 0, 0, length)
                    isHeadRead = true
                }
                digest.update(buffer, 0, contentSize)
            }
        }
        val digestValue = digest.digest().joinToString("") { "%02x".format(it) }
        val headValue = byteToString(headArray)
        return digestValue to headValue
    }

    private fun byteToString(byteArray: ByteArray): String {
        val stringBuilder = StringBuilder()
        byteArray.forEach {
            val byteValue = it.toInt() and 0xFF
            val hex = Integer.toHexString(byteValue)
            val hexValue = hex.padStart(2, '0').uppercase()
            stringBuilder.append(hexValue)
        }
        return stringBuilder.toString()
    }

    private fun charToCompareInt(c: Char): Int {
        return when(c) {
            in 'a'..'z' -> 200 + c.code
            in 'A'..'Z' -> 400 + c.code
            in '0'..'9' -> c.code
            else -> -1
        }
    }

}