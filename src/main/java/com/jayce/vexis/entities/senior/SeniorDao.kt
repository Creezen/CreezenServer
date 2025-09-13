package com.jayce.vexis.entities.senior

interface SeniorDao {
    fun addAdvice(seniorAdvice: SeniorAdvice)
    fun getAdvice(seniorAdvice: SeniorAdvice): List<SeniorAdvice>
}