package com.jayce.vexis.business.controllers

import com.creezen.commontool.bean.FileBean
import com.jayce.vexis.core.MyDispatchServlet
import com.jayce.vexis.business.dao.FileDao
import com.jayce.vexis.foundation.utils.FileHelper
import com.jayce.vexis.foundation.utils.FileHelper.getFileTypeByFileHead
import org.json.JSONObject
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile
import java.io.File

@Controller
class FileManager: MyDispatchServlet() {

    @RequestMapping(value = ["/fileUpload"])
    @ResponseBody
    fun upload(@RequestPart("fileEntry") fileBean: FileBean, @RequestPart("file") file: MultipartFile): Int {
        println("${file.originalFilename}   $fileBean")
        val session = sqlSessionFactory.openSession(true) ?: return -1
        val mapper = session.getMapper(FileDao::class.java)
        val filePair = FileHelper.getFileHashAndHead(file.inputStream, "SHA256")
        val fileHash = filePair.first
        val fileHead = filePair.second
        println("file hash: $fileHash   fileHead: $fileHead  fileType: ${getFileTypeByFileHead(fileHead)}")
        val existFile = mapper.findFileByHash(fileHash)
        if (existFile != null) {
            println("file exist")
            return -1
        }
        fileBean.fileHash = fileHash
        val destFile = File("$BASE_FILE_PATH${fileBean.fileID}${fileBean.fileSuffix}")
        file.transferTo(destFile)
        mapper.insertFile(fileBean)
        return 1
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