package com.jayce.vexis.foundation

import org.slf4j.LoggerFactory
import org.slf4j.MarkerFactory
import org.slf4j.spi.LocationAwareLogger

class Log(clazz: Class<*>, private val skipString: String? = null) {

    private val FQCN = this::class.java.name
    private val logger = LoggerFactory.getLogger(clazz) as LocationAwareLogger

    fun d(message: String, markerName: String) {
        log(message, LocationAwareLogger.DEBUG_INT, markerName, null)
    }

    fun d(message: String, throwable: Throwable) {
        log(message, LocationAwareLogger.DEBUG_INT, null, throwable)
    }

    fun d(message: String, markerName: String? = null, throwable: Throwable? = null) {
        log(message, LocationAwareLogger.DEBUG_INT, markerName, throwable)
    }

    fun i(message: String, markerName: String) {
        log(message, LocationAwareLogger.INFO_INT, markerName, null)
    }

    fun i(message: String, throwable: Throwable) {
        log(message, LocationAwareLogger.INFO_INT, null, throwable)
    }

    fun i(message: String, markerName: String? = null, throwable: Throwable? = null) {
        log(message, LocationAwareLogger.INFO_INT, markerName, throwable)
    }

    fun w(message: String, markerName: String) {
        log(message, LocationAwareLogger.WARN_INT, markerName, null)
    }

    fun w(message: String, throwable: Throwable) {
        log(message, LocationAwareLogger.WARN_INT, null, throwable)
    }

    fun w(message: String, markerName: String? = null, throwable: Throwable? = null) {
        log(message, LocationAwareLogger.WARN_INT, markerName, throwable)
    }

    fun e(message: String, markerName: String) {
        log(message, LocationAwareLogger.ERROR_INT, markerName, null)
    }

    fun e(message: String, throwable: Throwable) {
        log(message, LocationAwareLogger.ERROR_INT, null, throwable)
    }

    fun e(message: String, markerName: String? = null, throwable: Throwable? = null) {
        log(message, LocationAwareLogger.ERROR_INT, markerName, throwable)
    }

    private fun log(message: String, level: Int, markerName: String? = null, throwable: Throwable? = null) {
        val marker = markerName?.let { MarkerFactory.getMarker(it) }
        val displayMessage = if (markerName != null) "[${markerName}]$message" else message
        val fqcnName = if (skipString != null) findClassName() else FQCN
        logger.log(marker, fqcnName, level, displayMessage, null, throwable)
    }

    /**
     * 找名字，会跳过第一个找到的堆栈类名
     * 可以先通过Thread.currentThread().stackTrace遍历找到目标方法
     * 然后取上一行即可
     */
    private fun findClassName(): String {
        Thread.currentThread().stackTrace.forEach {
            val clzName = it.className
            if (clzName.contains(skipString ?: FQCN)) {
                return clzName
            }
        }
        return FQCN
    }
}