package com.jayce.vexis.business.controllers

import com.jayce.vexis.business.dao.ArticleDao
import com.jayce.vexis.core.MyDispatchServlet
import com.jayce.vexis.foundation.Log
import com.jayce.vexis.util.bean.*
import com.jayce.vexis.util.getRandomString
import com.jayce.vexis.util.toBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile
import java.io.File

@Controller
class ArticleManager : MyDispatchServlet() {

    private val log by lazy { Log(this::class.java) }

    @Autowired
    lateinit var articleDao: ArticleDao

    @RequestMapping("/postArticle")
    @ResponseBody
    @Transactional
    fun postArticle(
        @RequestParam articleTitle: String,
        @RequestParam userID: String,
        @RequestParam contents: String,
        @RequestPart("articleFile", required = false) articleFile: List<MultipartFile>?,
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
        val sections = contents.toBean<List<ArticleContentBean>>() ?: listOf()
        var fileIndex = 0
        sections.forEachIndexed { index, sec ->
            val content = when (sec.type) {
                0 -> sec.content
                1 -> {
                    articleFile?.get(fileIndex)?.let {
                        val fileId = "${getRandomString(6)}${System.currentTimeMillis()}"
                        val suffixIndex = it.originalFilename?.lastIndexOf(".") ?: 0
                        val fileSuffix = it.originalFilename?.substring(suffixIndex)
                        val destFile = File("$BASE_FILE_PATH${fileId}${fileSuffix}")
                        it.transferTo(destFile)
                        fileIndex++
                        "${fileId}${fileSuffix}"
                    } ?: run { "" }
                }
                else -> ""
            }
            val sectionBean = SectionBean (
                article.articleId,
                -1,
                index,
                sec.type,
                content
            )
            articleDao.saveSection(sectionBean)
        }
        return true
    }

    @RequestMapping("getArticle")
    @ResponseBody
    fun getArticle(): List<ArticleBean> = articleDao.getArticle()

    @RequestMapping("getSection")
    @ResponseBody
    @Transactional
    fun getSection(articleId: Long): List<SectionRemarkBean> {
        val sectionList = articleDao.getSections(articleId)
        val remarkList = arrayListOf<SectionRemarkBean>()
        sectionList.forEach {
            val sectionRemarkList = articleDao.getRemark(it.sectionId)
            val sectionRemarkBean = SectionRemarkBean(
                it.articleId,
                it.sectionId,
                it.type,
                it.content,
                sectionRemarkList
            )
            remarkList.add(sectionRemarkBean)
        }
        return remarkList
    }

    @RequestMapping("postRemark")
    @ResponseBody
    fun postRemark(sectionId: Long, userId: String, content: String, type: Int): Boolean {
        log.d("receive remark:  $content")
        articleDao.insertRemark(RemarkBean(
            sectionId,
            userId,
            0L,
            content,
            type,
            0,
            System.currentTimeMillis()
        ))
        return true
    }

    @RequestMapping("deleteArticle")
    @ResponseBody
    fun deleteArticle(articleId: Long): Boolean {
        articleDao.deleteArticle(articleId)
        return true
    }
}