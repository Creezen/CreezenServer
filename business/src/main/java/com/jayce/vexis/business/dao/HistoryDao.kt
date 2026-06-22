package com.jayce.vexis.business.dao

import com.jayce.vexis.util.bean.HistoryBean

interface HistoryDao {

    fun insertEvent(historyBean: HistoryBean)

    fun queryAllEvent(): List<HistoryBean>
}