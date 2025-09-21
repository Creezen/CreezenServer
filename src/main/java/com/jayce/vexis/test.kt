package com.jayce.vexis

import com.creezen.commontool.Util.loadDataFromYAML
import com.creezen.commontool.bean.TransferStatusBean
import java.io.File
import java.io.FileInputStream

fun main() {
    println("F: ${'F'.code}  f: ${'f'.code}  9: ${'9'.code}" )

//    val file = File("C:\\Users\\LJW\\Desktop\\temp")
//    file.listFiles()?.forEach {
//        val byteString = getFileByte(it, 20)
//        println(byteString)
//    }


//    val map = loadDataFromYAML()
//    val compare = Comparator<String> { a, b ->
//        val compResult = b.length - a.length
//        if (compResult != 0) {
//            return@Comparator compResult
//        }
//        a.zip(b).forEach {
//            if (it.first == it.second) {
//                return@forEach
//            }
//            return@Comparator charToCompareInt(it.first) - charToCompareInt(it.second)
//        }
//        return@Comparator 0
//    }
//    val keyList = map.keys.toList().sortedWith(compare)
//
//    keyList.forEach {
//        println("key: $it")
//    }
}

private fun charToCompareInt(c: Char): Int {
    return when(c) {
        in 'a'..'z' -> 200 + c.code
        in 'A'..'Z' -> 400 + c.code
        in '0'..'9' -> c.code
        else -> -1
    }
}

fun getFileByte(file: File, length: Int = 10): String {
    runCatching {
        val array = ByteArray(length)
        FileInputStream(file).use {
            it.read(array, 0, length)
            return byteToString(array)
        }
    }.onFailure {
        println("getFileByte error: ${it.message}")
    }
    return ""
}

private fun byteToString(byteArray: ByteArray): String {
    val stringBuilder = StringBuilder()
    byteArray.forEach {
        val byteValue = it.toInt() and 0xFF
        val hex = Integer.toHexString(byteValue)
        val hexValue = hex.padStart(2, '0')
        stringBuilder.append(hexValue)
    }
    return stringBuilder.toString()
}
