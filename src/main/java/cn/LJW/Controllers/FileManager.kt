package cn.LJW.Controllers

import cn.LJW.Entities.Resource.FileBean
import cn.LJW.Entities.Resource.ResourceDao
import cn.LJW.MyDispatchServlet
import org.json.JSONObject
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile
import java.io.File

@Controller
class FileManager: MyDispatchServlet() {

    @RequestMapping(value = ["/fileUpload"])
    @ResponseBody
    fun upload(fileBean: FileBean, file: MultipartFile): String {
        println("${file.originalFilename}   $fileBean")
        val session = sqlSessionFactory?.openSession(true) ?: return ""
        val mapper = session.getMapper(ResourceDao::class.java)
        file.transferTo(File("C:/Users/LJW/Desktop/store/${fileBean.fileID}${fileBean.fileSuffix}"))
        mapper.insertFile(fileBean)
        return JSONObject().apply {
            put("loadResult", true)
        }.toString()
    }

    @RequestMapping(value = ["/fileFetch"])
    @ResponseBody
    fun fetch(): String {
        val session = sqlSessionFactory?.openSession(true) ?: return ""
        val mapper = session.getMapper(ResourceDao::class.java)
        val resItems = mapper.getFile()
        return JSONObject().apply {
            put("items", resItems)
        }.toString()
    }
}