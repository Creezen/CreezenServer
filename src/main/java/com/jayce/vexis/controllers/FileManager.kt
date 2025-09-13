package com.jayce.vexis.controllers

import com.jayce.vexis.MyDispatchServlet
import com.jayce.vexis.entities.file.FileBean
import com.jayce.vexis.entities.file.FileDao
import com.jayce.vexis.utils.FileHelper
import org.json.JSONObject
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile
import java.io.File

@Controller
class FileManager: MyDispatchServlet() {

    @RequestMapping(value = ["/fileUpload"])
    @ResponseBody
    fun upload(@RequestPart("fileEntry") fileBean: FileBean, @RequestPart("file") file: MultipartFile): String {
        println("${file.originalFilename}   $fileBean")
        val session = sqlSessionFactory.openSession(true) ?: return ""
        val mapper = session.getMapper(FileDao::class.java)
        val fileHash = FileHelper.getFileHash(file.inputStream, "SHA256")
        println("file hash: $fileHash")
        val existFile = mapper.findFileByHash(fileHash)
        if (existFile != null) {
            println("file exist")
            return "-1"
        }
        fileBean.fileHash = fileHash
        val destFile = File("$BASE_FILE_PATH${fileBean.fileID}${fileBean.fileSuffix}")
        file.transferTo(destFile)
        mapper.insertFile(fileBean)
        return JSONObject().apply {
            put("loadResult", true)
        }.toString()
    }

    @RequestMapping(value = ["/fileFetch"])
    @ResponseBody
    fun fetch(): String {
        val session = sqlSessionFactory.openSession(true) ?: return ""
        val mapper = session.getMapper(FileDao::class.java)
        val resItems = mapper.getFile()
        return JSONObject().apply {
            put("items", resItems)
        }.toString()
    }
}