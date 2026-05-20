package com.jayce.vexis.foundation

import org.aspectj.lang.ProceedingJoinPoint

class RedisAspect {

    private val log by lazy { Log(this::class.java, "SpringCGLIB") }

    fun intercept(jointPoint: ProceedingJoinPoint): Any? {
        val startTime = System.currentTimeMillis()
        val result = jointPoint.proceed()
        val endTime = System.currentTimeMillis()
        val method = jointPoint.signature.toShortString()
        val args = jointPoint.args
        val argValue = args.map { "$it, " }.toString()
        val tailLength = if (args.isNotEmpty()) 2 else 0
        val methodLog = method.subSequence(0, method.length - 2 - tailLength)
        val argLog = argValue.subSequence(1, argValue.length - 1 - tailLength)
        val duration = endTime - startTime
        log.d("[redis - ${duration}ms] ${methodLog}(${argLog})  Result: $result")
        return result
    }
}