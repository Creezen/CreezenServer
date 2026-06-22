package com.jayce.vexis.util

import com.jayce.vexis.util.Config.BASIC_LETTER
import com.jayce.vexis.util.Config.NIL
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random
import java.util.TimeZone

fun getRandomString(length: Int) : String {
    val random = Random()
    val buffer = StringBuffer()
    for (i in 0 until length) {
        buffer.append(BASIC_LETTER[random.nextInt(52)])
    }
    return buffer.toString()
}

fun Long.toTime(formater: String = "yyyy-MM-dd HH:mm:ss"):String{
    val simpleDateFormat= SimpleDateFormat(formater, Locale.CHINA)
    simpleDateFormat.timeZone= TimeZone.getTimeZone("GMT+8")
    if (formater.isNotEmpty()) return simpleDateFormat.format(Date(this))
    return NIL
}

inline fun <reified T> String.toBean(): T? {
    runCatching {
        val type: Type = object : TypeToken<T>(){}.type
        return Gson().fromJson(this, type)
    }.onFailure {
        it.printStackTrace()
    }
    return null
}

fun Any.toJson(): String {
    runCatching {
        return Gson().toJson(this)
    }.onFailure {
       it.printStackTrace()
    }
    return NIL
}

fun isLeapYear(year: Int) = when {
    year % 100 == 0 -> year % 400 == 0
    year % 4 == 0 -> true
    else -> false
}

fun getMaxDayOfMonth(year: Int, month: Int): Int {
    val isLeap = isLeapYear(year)
    return when (month) {
        1,3,5,7,8,10,12 -> 31
        4,6,9,11 -> 30
        else -> if (isLeap) 29 else 28
    }
}