package com.jayce.vexis.entities.article

data class ArticleBean(
    var synergyId: Long = 0,
    var sUID: String = "",
    var title: String = "",
    var createTime: Long = 0,
    var updateTime: Long = 0,
    var favor: Long = 0
)