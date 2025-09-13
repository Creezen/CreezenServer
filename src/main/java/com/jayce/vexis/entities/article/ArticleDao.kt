package com.jayce.vexis.entities.article

interface ArticleDao {

    fun saveArticle(articleBean: ArticleBean)

    fun saveParagraph(paragraphBean: ParagraphBean)

    fun getArticle(): List<ArticleBean>

    fun getParagraph(synergyId: Long): List<ParagraphBean>

    fun getComment(paragraphId: Long): List<CommentBean>

    fun insertComment(commentBean: CommentBean)
}