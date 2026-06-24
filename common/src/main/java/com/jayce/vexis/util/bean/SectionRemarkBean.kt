package com.jayce.vexis.util.bean

data class SectionRemarkBean(
    val articleId: Long,
    val sectionId: Long,
    val type : Int,
    val content: String,
    val list: List<RemarkBean>,
)
