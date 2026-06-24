package com.jayce.vexis.business.controllers

import com.jayce.vexis.business.dao.FileDao
import com.jayce.vexis.core.MyDispatchServlet
import com.jayce.vexis.foundation.Log
import com.jayce.vexis.foundation.utils.FileHelper
import com.jayce.vexis.util.bean.FileBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

@Controller
class FileManager: MyDispatchServlet() {

    private val log by lazy { Log(this::class.java) }

    @Autowired
    lateinit var fileDao: FileDao

    @RequestMapping(value = ["/fileUpload"])
    @ResponseBody
    fun upload(
        @RequestPart("fileEntry") fileBean: FileBean,
        @RequestPart("file") file: MultipartFile,
    ): Int {
        log.d("${file.originalFilename}   $fileBean")
        val tempFile = FileHelper.tempFile()
        file.transferTo(tempFile)
        val filePair = FileHelper.getFileHashAndType(tempFile, "SHA256")
        val fileHash = filePair.first
        val fileType = filePair.second
        val bean = if (fileType.isNotEmpty()) {
            fileBean.copy(fileSuffix = ".$fileType")
        } else {
            fileBean
        }
        val existFile = fileDao.findFileByHash(fileHash)
        if (existFile != null) {
            log.d("file exist")
            return -1
        }
        bean.fileHash = fileHash
        val destFile = File("$BASE_FILE_PATH${bean.fileID}${bean.fileSuffix}")
        destFile.parentFile?.mkdirs()
        Files.move(tempFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        fileDao.insertFile(bean)
        return 1
    }

    @RequestMapping(value = ["/fileFetch"])
    @ResponseBody
    fun fetch(): List<FileBean> {
        return fileDao.getFile()
    }
}