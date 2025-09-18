package com.jayce.vexis.dao

import com.creezen.commontool.bean.HistoryBean

interface HistoryDao {

    fun insertEvent(historyBean: HistoryBean)

    fun queryAllEvent(): List<HistoryBean>
}