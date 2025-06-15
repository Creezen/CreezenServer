package cn.LJW.Utils

import java.io.File
import java.security.MessageDigest

object FileHelper {

    fun getFileHash(file: File, algorithm: String): String {
        val digest = MessageDigest.getInstance(algorithm)
        file.inputStream().use { stream ->
            val buffer = ByteArray(1 * 1024 * 1024)
            var contentSize = 0
            while (stream.read(buffer).also { contentSize = it } != -1) {
                digest.update(buffer, 0, contentSize)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

}