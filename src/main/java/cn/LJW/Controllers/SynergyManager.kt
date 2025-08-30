package cn.LJW.Controllers

import cn.LJW.Entities.article.*
import cn.LJW.MyDispatchServlet
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.math.BigInteger

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
    fun getArticle(synergyId: Long): List<ParagraphCommandBean> {
        val session = sqlSessionFactory.openSession(false) ?: return arrayListOf()
        val mapper = session.getMapper(ArticleDao::class.java)
        val paragraphList = mapper.getParagraph(synergyId)
        val paragraphCommandList = arrayListOf<ParagraphCommandBean>()
        paragraphList.forEach {
            val commandList = mapper.getComment(it.paragraphId)
            val paragraphCommandBean = ParagraphCommandBean(
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