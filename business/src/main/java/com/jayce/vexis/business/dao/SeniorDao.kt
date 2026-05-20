package com.jayce.vexis.business.dao

import com.creezen.commontool.bean.PeerAdviceBean

interface SeniorDao {
    fun addAdvice(peerAdviceBean: PeerAdviceBean)
    fun getAdvice(peerAdviceBean: PeerAdviceBean): List<PeerAdviceBean>
}