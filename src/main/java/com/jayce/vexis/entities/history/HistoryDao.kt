package com.jayce.vexis.entities.history

interface HistoryDao {

    fun insertEvent(historyBean: HistoryBean)

    fun queryAllEvent(): List<HistoryBean>
}