package com.jayce.vexis.util.bean

import com.jayce.vexis.util.Config.NIL

data class ArticleBean(
    var articleId: Long = 0,
    var userId: String = NIL,
    var title: String = NIL,
    var createTime: Long = 0,
    var updateTime: Long = 0,
    var favor: Long = 0
)
