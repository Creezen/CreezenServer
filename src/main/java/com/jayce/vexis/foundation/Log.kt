package com.jayce.vexis.foundation

import org.slf4j.LoggerFactory
import org.slf4j.MarkerFactory
import org.slf4j.spi.LocationAwareLogger

class Log(private val clazz: Class<*>) {

    private val className = this::class.java.name
    private val logger by lazy { LoggerFactory.getLogger(clazz) as LocationAwareLogger }

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
        val displayMessage = if (markerName != null) {
            "[${markerName}]$message"
        } else message
        logger.log(marker, className, level, displayMessage, null, throwable)
    }
}