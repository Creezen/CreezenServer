package com.jayce.vexis.business.dao

import com.jayce.vexis.util.bean.ArticleBean
import com.jayce.vexis.util.bean.RemarkBean
import com.jayce.vexis.util.bean.SectionBean

interface ArticleDao {

    fun saveArticle(articleBean: ArticleBean)

    fun saveParagraph(sectionBean: SectionBean)

    fun getArticle(): List<ArticleBean>

    fun getSections(articleId: Long): List<SectionBean>

    fun getComment(paragraphId: Long): List<RemarkBean>

    fun insertComment(remarkBean: RemarkBean)
}