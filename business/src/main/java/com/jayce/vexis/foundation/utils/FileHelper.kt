package com.jayce.vexis.foundation.utils

import com.jayce.vexis.core.MyDispatchServlet.Companion.BASE_FILE_PATH
import com.jayce.vexis.foundation.Log
import com.jayce.vexis.util.bean.FileType
import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import java.io.File
import java.io.InputStream
import java.security.MessageDigest
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import kotlin.math.min

object FileHelper {

    private val log by lazy { Log(this::class.java) }

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
        if (!FileHelper::fileTypeList.isInitialized) {
            fileTypeList = fileTypeMap.keys.toList().sortedWith(compare)
        }
    }

    fun getFileHashAndType(file: File, algorithm: String, length: Int = 1024): Pair<String, String> {
        val digest = MessageDigest.getInstance(algorithm)
        val headArray = ByteArray(length)
        var isHeadRead = false
        var readLength = 0
        file.inputStream().use { stream ->
            val buffer = ByteArray(1024 * 1024)
            var contentSize: Int
            while (stream.read(buffer).also { contentSize = it } != -1) {
                if (!isHeadRead) {
                    readLength = min(contentSize, length)
                    buffer.copyInto(headArray, 0, 0, readLength)
                    isHeadRead = true
                }
                digest.update(buffer, 0, contentSize)
            }
        }
        val digestValue = digest.digest().joinToString("") { "%02x".format(it) }
        val headValue = byteToString(headArray.copyOfRange(0, readLength))
        val fileType = getFileTypeByFileHead(file, headValue)
        return digestValue to fileType
    }

    fun getFileTypeByFileHead(file: File, fileHead: String): String {
        for (i in fileTypeList.indices) {
            val typeIndicator = fileTypeList[i]
            if (fileHead.length < typeIndicator.length) {
                continue
            }
            if (fileHead.startsWith(typeIndicator)) {
                var type = fileTypeMap[typeIndicator]?.first()
                if (type == "zip") {
                    if (isApk(file)) {
                        type = "apk"
                    }
                }
                return type ?: ""
            }
        }
        return ""
    }

    fun isApk(file: File): Boolean {
        var isApkFile: Boolean
        ZipFile(file).use {
            val hasManifest = it.getEntry("AndroidManifest.xml") != null
            val hasResources = it.getEntry("resources.arsc") != null
            val hasClasses = it.getEntry("classes.dex") != null
            isApkFile = hasManifest && (hasResources || hasClasses)
        }
        return isApkFile
    }

    fun isApk(stream: InputStream): Boolean {
        ZipInputStream(stream).use { zip ->
            var entry = zip.nextEntry
            var hasManifest = false
            var hasResource = false
            var hasClasses = false
            while (entry != null) {
                when (entry.name) {
                    "AndroidManifest.xml" -> hasManifest = true
                    "resources.arsc" -> hasResource = true
                    "classes.dex" -> hasClasses = true
                }
                if (hasManifest && (hasResource || hasClasses)) {
                    return true
                }
                entry = zip.nextEntry
            }
        }
        return false
    }

    fun tempFile(): File {
        val tempDirectory = File("$BASE_FILE_PATH/temp/")
        if (tempDirectory.exists().not()) tempDirectory.mkdirs()
        val tempFile = File(tempDirectory, "${System.currentTimeMillis()}")
        tempFile.createNewFile()
        return tempFile
    }

    private fun loadDataFromYAML(): Map<String, List<String>> {
        kotlin.runCatching {
            val yaml = Yaml(Constructor(LoaderOptions()))
            val source = "fileType.yaml"
            javaClass.classLoader.getResourceAsStream(source)?.use {
                val values = yaml.loadAs(it, FileType::class.java)
                return values.typeMap
            } ?: run {
                println("stream is empty")
            }
        }.onFailure {
            println("fail ${it.message}")
        }
        return mapOf()
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