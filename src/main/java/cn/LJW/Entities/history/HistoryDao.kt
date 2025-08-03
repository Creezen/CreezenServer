package cn.LJW.Entities.history

interface HistoryDao {

    fun insertEvent(historyBean: HistoryBean)

}