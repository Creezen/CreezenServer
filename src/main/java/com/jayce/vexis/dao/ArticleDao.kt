package com.jayce.vexis.dao

import com.creezen.commontool.bean.ArticleBean
import com.creezen.commontool.bean.RemarkBean
import com.creezen.commontool.bean.SectionBean

interface ArticleDao {

    fun saveArticle(articleBean: ArticleBean)

    fun saveParagraph(sectionBean: SectionBean)

    fun getArticle(): List<ArticleBean>

    fun getParagraph(synergyId: Long): List<SectionBean>

    fun getComment(paragraphId: Long): List<RemarkBean>

    fun insertComment(remarkBean: RemarkBean)
}