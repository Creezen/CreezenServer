package com.jayce.vexis.business.controllers

import com.creezen.commontool.bean.ArticleBean
import com.creezen.commontool.bean.RemarkBean
import com.creezen.commontool.bean.SectionBean
import com.creezen.commontool.bean.SectionRemarkBean
import com.jayce.vexis.core.MyDispatchServlet
import com.jayce.vexis.business.dao.ArticleDao
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.math.BigInteger

@Controller
class ArticleManager: MyDispatchServlet() {

    private val mapper by lazy {
        val session = sqlSessionFactory.openSession(true)
        session.getMapper(ArticleDao::class.java)
    }

    @RequestMapping("/postSynergy")
    @ResponseBody
    fun saveSynergy(
        articleTitle: String,
        @RequestParam paragraphs: ArrayList<String>,
        userID: String
    ): Boolean {
        val time = System.currentTimeMillis()
        val session = sqlSessionFactory.openSession(false) ?: return false
        val mapper = session.getMapper(ArticleDao::class.java)
        val article = ArticleBean().apply {
            this.userId = userID
            title = articleTitle
            createTime = time
            updateTime = time
            favor = 0
        }
        mapper.saveArticle(article)
        paragraphs.forEach {
            val sectionBean = SectionBean().apply {
                articleId = article.articleId
                content = it
            }
            mapper.saveParagraph(sectionBean)
        }
        session.commit()
        return true
    }

    @RequestMapping("getSynergy")
    @ResponseBody
    fun getSynergy(): List<ArticleBean> {
        return mapper.getArticle()
    }

    @RequestMapping("getSection")
    @ResponseBody
    fun getSection(articleId: Long): List<SectionRemarkBean> {
        val session = sqlSessionFactory.openSession(false) ?: return arrayListOf()
        val mapper = session.getMapper(ArticleDao::class.java)
        val paragraphList = mapper.getSections(articleId)
        val paragraphCommandList = arrayListOf<SectionRemarkBean>()
        paragraphList.forEach {
            val commandList = mapper.getComment(it.sectionId)
            val paragraphCommandBean = SectionRemarkBean(
                it.sectionId,
                it.content,
                commandList
            )
            paragraphCommandList.add(paragraphCommandBean)
        }
        session.commit()
        return paragraphCommandList
    }

    @RequestMapping("postCommen")
    @ResponseBody
    fun postCommen(
        articleId: BigInteger,
        paragraphId: Long,
        userId: String,
        comment: String
    ): Boolean {
        println("receive commen:  $comment")
        mapper.insertComment(RemarkBean(
            articleId,
            paragraphId,
            userId,
            -1,
            comment,
            0,
            System.currentTimeMillis().toBigInteger()
        ))
        return true
    }
}