package cn.LJW.Utils

import java.io.File
import java.io.InputStream
import java.security.MessageDigest

object FileHelper {

    fun getFileHash(file: File, algorithm: String): String {
        return getFileHash(file.inputStream(), algorithm)
//        val digest = MessageDigest.getInstance(algorithm)
//        file.inputStream().use { stream ->
//            val buffer = ByteArray(1 * 1024 * 1024)
//            var contentSize = 0
//            while (stream.read(buffer).also { contentSize = it } != -1) {
//                digest.update(buffer, 0, contentSize)
//            }
//        }
//        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    fun getFileHash(stream: InputStream, algorithm: String): String {
        val digest = MessageDigest.getInstance(algorithm)
        stream.use {
            val buffer = ByteArray(1 * 1024 * 1024)
            var contentSize = 0
            while (stream.read(buffer).also { contentSize = it } != -1) {
                digest.update(buffer, 0, contentSize)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

}