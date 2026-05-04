package com.jayce.vexis.business.controllers

import com.creezen.commontool.bean.FileBean
import com.jayce.vexis.core.MyDispatchServlet
import com.jayce.vexis.business.dao.FileDao
import com.jayce.vexis.foundation.Log
import com.jayce.vexis.foundation.utils.FileHelper
import com.jayce.vexis.foundation.utils.FileHelper.getFileTypeByFileHead
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.zip.ZipFile

@Controller
class FileManager: MyDispatchServlet() {

    private val log by lazy { Log(this::class.java) }

    @Autowired
    lateinit var fileDao: FileDao

    @RequestMapping(value = ["/fileUpload"])
    @ResponseBody
    fun upload(@RequestPart("fileEntry") fileBean: FileBean, @RequestPart("file") file: MultipartFile): Int {
        log.d("${file.originalFilename}   $fileBean")
        val filePair = FileHelper.getFileHashAndHead(file.inputStream, "SHA256")
        val fileHash = filePair.first
        val fileHead = filePair.second
        log.d("file hash: $fileHash   fileHead: $fileHead  fileType: ${getFileTypeByFileHead(fileHead)}")
        val existFile = fileDao.findFileByHash(fileHash)
        if (existFile != null) {
            log.d("file exist")
            return -1
        }
        fileBean.fileHash = fileHash
        val destFile = File("$BASE_FILE_PATH${fileBean.fileID}${fileBean.fileSuffix}")
        file.transferTo(destFile)
        fileDao.insertFile(fileBean)
        return 1
    }

    @RequestMapping(value = ["/aaa"])
    @ResponseBody
    fun aaa(): Boolean {
        val afile = File("C:\\Users\\LJW\\Desktop\\temp")
        afile.listFiles()?.forEach {
            val fileHead = FileHelper.getFileHashAndHead(it, "SHA256").second
            val fileType = getFileTypeByFileHead(fileHead)
            log.d("file name: ${it.name}   fileHead: $fileHead  fileType: $fileType")
            if (fileType == "zip") {
                isApk(it)
            }
        }
        return true
    }

    private fun isApk(file: File) {
        var isApkFile = false
        ZipFile(file).use {
            val hasManifest = it.getEntry("AndroidManifest.xml") != null
            val hasResources = it.getEntry("resources.arsc") != null
            val hasClasses = it.getEntry("classes.dex") != null
            log.d("hasManifest: $hasManifest  hasResources: $hasResources   hasClasses:  $hasClasses")
            isApkFile = hasManifest && (hasResources || hasClasses)
        }
        log.d("isApk: $isApkFile")
    }

    @RequestMapping(value = ["/fileFetch"])
    @ResponseBody
    fun fetch(): String {
        val resItems = fileDao.getFile()
        return JSONObject().apply {
            put("items", resItems)
        }.toString()
    }
}