package com.jayce.vexis.business.dao

import com.jayce.vexis.util.bean.ArticleBean
import com.jayce.vexis.util.bean.RemarkBean
import com.jayce.vexis.util.bean.SectionBean

interface ArticleDao {

    fun saveArticle(articleBean: ArticleBean)

    fun saveSection(sectionBean: SectionBean)

    fun getArticle(): List<ArticleBean>

    fun getSections(articleId: Long): List<SectionBean>

    fun getRemark(sectionId: Long): List<RemarkBean>

    fun insertRemark(remarkBean: RemarkBean)

    fun deleteArticle(articleId: Long)
}