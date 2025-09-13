package com.jayce.vexis.controllers

import com.jayce.vexis.entities.article.*
import com.jayce.vexis.MyDispatchServlet
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class SynergyManager: MyDispatchServlet() {

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
            sUID = userID
            title = articleTitle
            createTime = time
            updateTime = time
            favor = 0
        }
        mapper.saveArticle(article)
        paragraphs.forEach {
            val paragraphBean = ParagraphBean().apply {
                synergyId = article.synergyId
                content = it
            }
            mapper.saveParagraph(paragraphBean)
        }
        session.commit()
        return true
    }

    @RequestMapping("getSynergy")
    @ResponseBody
    fun getSynergy(): List<ArticleBean> {
        return mapper.getArticle()
    }

    @RequestMapping("getArticleFragment")
    @ResponseBody
    fun getArticle(synergyId: Long): List<com.jayce.vexis.entities.article.ParagraphCommandBean> {
        val session = sqlSessionFactory.openSession(false) ?: return arrayListOf()
        val mapper = session.getMapper(ArticleDao::class.java)
        val paragraphList = mapper.getParagraph(synergyId)
        val paragraphCommandList = arrayListOf<com.jayce.vexis.entities.article.ParagraphCommandBean>()
        paragraphList.forEach {
            val commandList = mapper.getComment(it.paragraphId)
            val paragraphCommandBean = com.jayce.vexis.entities.article.ParagraphCommandBean(
                it.paragraphId,
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
        synergyId: Long,
        paragraphId: Long,
        userId: String,
        comment: String
    ): Boolean {
        println("receive commen:  $comment")
        mapper.insertComment(CommentBean(
            synergyId.toBigInteger(),
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