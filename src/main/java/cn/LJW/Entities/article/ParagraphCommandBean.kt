package cn.LJW.Entities.article

data class ParagraphCommandBean(
    val paragraphId: Long,
    val content: String,
    val list: List<CommentBean>
)