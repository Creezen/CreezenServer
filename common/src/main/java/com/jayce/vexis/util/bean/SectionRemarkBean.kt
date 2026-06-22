package com.jayce.vexis.util.bean

import com.jayce.vexis.util.bean.RemarkBean

data class SectionRemarkBean(
    val sectionId: Long,
    val content: String,
    val list: List<RemarkBean>,
)
