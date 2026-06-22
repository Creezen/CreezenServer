package com.jayce.vexis.util.bean

import com.jayce.vexis.util.Config.LONG_ZERO
import com.jayce.vexis.util.Config.NIL

data class SectionBean (
    var sectionId: Long = LONG_ZERO,
    var articleId: Long = LONG_ZERO,
    var content: String = NIL
)