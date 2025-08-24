package cn.LJW.Entities.senior

interface SeniorDao {
    fun addAdvice(seniorAdvice: SeniorAdvice)
    fun getAdvice(seniorAdvice: SeniorAdvice): List<SeniorAdvice>
}