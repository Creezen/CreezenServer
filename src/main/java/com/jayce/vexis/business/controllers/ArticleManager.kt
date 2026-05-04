package com.jayce.vexis.business.controllers

import com.creezen.commontool.bean.ArticleBean
import com.creezen.commontool.bean.RemarkBean
import com.creezen.commontool.bean.SectionBean
import com.creezen.commontool.bean.SectionRemarkBean
import com.jayce.vexis.core.MyDispatchServlet
import com.jayce.vexis.business.dao.ArticleDao
import com.jayce.vexis.foundation.Log
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.math.BigInteger

@Controller
class ArticleManager: MyDispatchServlet() {

    private val log by lazy { Log(this::class.java) }

    @Autowired
    lateinit var articleDao: ArticleDao

    @RequestMapping("/postSynergy")
    @ResponseBody
    @Transactional
    fun saveSynergy(
        articleTitle: String,
        @RequestParam paragraphs: ArrayList<String>,
        userID: String
    ): Boolean {
        val time = System.currentTimeMillis()
        val article = ArticleBean().apply {
            this.userId = userID
            title = articleTitle
            createTime = time
            updateTime = time
            favor = 0
        }
        articleDao.saveArticle(article)
        paragraphs.forEach {
            val sectionBean = SectionBean().apply {
                articleId = article.articleId
                content = it
            }
            articleDao.saveParagraph(sectionBean)
        }
        return true
    }

    @RequestMapping("getSynergy")
    @ResponseBody
    fun getSynergy(): List<ArticleBean> {
        return articleDao.getArticle()
    }

    @RequestMapping("getSection")
    @ResponseBody
    @Transactional
    fun getSection(articleId: Long): List<SectionRemarkBean> {
        val paragraphList = articleDao.getSections(articleId)
        val paragraphCommandList = arrayListOf<SectionRemarkBean>()
        paragraphList.forEach {
            val commandList = articleDao.getComment(it.sectionId)
            val paragraphCommandBean = SectionRemarkBean(
                it.sectionId,
                it.content,
                commandList
            )
            paragraphCommandList.add(paragraphCommandBean)
        }
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
        log.d("receive commen:  $comment")
        articleDao.insertComment(RemarkBean(
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