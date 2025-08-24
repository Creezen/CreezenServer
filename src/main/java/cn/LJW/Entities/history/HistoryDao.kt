package cn.LJW.Entities.history

interface HistoryDao {

    fun insertEvent(historyBean: HistoryBean)

    fun queryAllEvent(): List<HistoryBean>
}