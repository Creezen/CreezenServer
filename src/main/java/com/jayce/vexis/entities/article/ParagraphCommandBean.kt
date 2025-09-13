package com.jayce.vexis.entities.article

data class ParagraphCommandBean(
    val paragraphId: Long,
    val content: String,
    val list: List<com.jayce.vexis.entities.article.CommentBean>
)