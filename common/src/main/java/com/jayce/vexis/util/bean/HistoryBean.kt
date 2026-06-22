package com.jayce.vexis.util.bean

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class HistoryBean (
    val time: String,
    val event: String
) {

    fun isValid() = time.length == 17

    fun millisTime(): Long {
        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
        val localDateTime = LocalDateTime.parse(time, formatter)
        val zoneDateTime = localDateTime.atZone(ZoneId.systemDefault())
        return zoneDateTime.toInstant().toEpochMilli()
    }
}
