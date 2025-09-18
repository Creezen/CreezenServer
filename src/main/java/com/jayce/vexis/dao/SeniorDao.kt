package com.jayce.vexis.dao

import com.creezen.commontool.bean.PeerAdviceBean

interface SeniorDao {
    fun addAdvice(seniorAdvice: PeerAdviceBean)
    fun getAdvice(seniorAdvice: PeerAdviceBean): List<PeerAdviceBean>
}