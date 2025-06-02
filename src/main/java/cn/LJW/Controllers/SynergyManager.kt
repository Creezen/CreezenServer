package cn.LJW.Controllers

import cn.LJW.Entities.article.ArticleBean
import cn.LJW.Entities.article.ArticleDao
import cn.LJW.Entities.article.ParagraphBean
import cn.LJW.Entities.article.ParagraphCommandBean
import cn.LJW.MyDispatchServlet
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class SynergyManager: MyDispatchServlet() {

    @RequestMapping("/postSynergy")
    @ResponseBody
    fun saveSynergy(
        @RequestParam paragraphs: ArrayList<String>,
        userID: String
    ): Boolean {
        val time = System.currentTimeMillis()
        val session = sqlSessionFactory?.openSession(false) ?: return false
        val mapper = session.getMapper(ArticleDao::class.java)
        val article = ArticleBean().apply {
            sUID = userID
            title = "new paper"
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
        val session = sqlSessionFactory?.openSession(true) ?: return arrayListOf()
        val mapper = session.getMapper(ArticleDao::class.java)
        return mapper.getArticle()
    }

    @RequestMapping("getArticle")
    @ResponseBody
    fun getArticle(synergyId: Long): List<ParagraphCommandBean> {
        val session = sqlSessionFactory?.openSession(false) ?: return arrayListOf()
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
    fun postCommen(commen: String): Boolean {
        println("receive commen:  $commen")
        return true
    }
}