package com.jayce.vexis.business.controllers

import com.jayce.vexis.util.bean.ApkSimpleInfo
import com.jayce.vexis.core.MyDispatchServlet
import com.jayce.vexis.util.getRandomString
import com.jayce.vexis.foundation.Log
import com.jayce.vexis.util.bean.ApkInfoBean
import net.dongliu.apk.parser.ApkFile
import net.dongliu.apk.parser.bean.ApkMeta
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile
import java.io.File

@Controller
class PackageManager: MyDispatchServlet() {

    private val log by lazy { Log(this::class.java) }
    private val apkBasePath
        get() = "${BASE_FILE_PATH}APK/"

    @RequestMapping(value = ["/checkApkInfo"])
    @ResponseBody
    fun checkApkInfo(@RequestParam("apkFile") file: MultipartFile): ApkInfoBean {
        val tempName = getRandomString(20)
        val tempPath = "$apkBasePath$tempName.apk"
        val tempFile = File(tempPath)
        file.transferTo(tempFile)
        val metaData = resolveApkFile(tempFile)
        val versionName = metaData.versionName
        val versionCode = metaData.versionCode
        val fileSize_M = (tempFile.length() / 1024 / 1024)
        val fileSize = "$fileSize_M M"
        log.d("versionName: $versionName  versionCode: $versionCode")
        tempFile.delete()
        return ApkInfoBean(versionCode, versionName, fileSize)
    }

    @RequestMapping(value = ["/uploadApk"])
    @ResponseBody
    fun uploadApk(
        @RequestParam("versionCode") versionCode: Long,
        @RequestParam("versionName") versionName: String,
        @RequestParam("apkFile") file: MultipartFile
    ): String {
        val destFile = File("$apkBasePath$versionCode")
        if (destFile.exists()) return "文件已存在"
        if (versionCode <= getMaxVersion()) return "APK版本号过低"
        destFile.mkdir()
        file.transferTo(File("${destFile.path}/$versionName.apk"))
        return "文件上传成功"
    }

    @RequestMapping(value = ["/checkVersion"])
    @ResponseBody
    fun checkVersion(): ApkSimpleInfo {
        val maxVersion = getMaxVersion()
        val fileDirectory = "${apkBasePath}$maxVersion"
        log.d("maxversion: $maxVersion $fileDirectory")
        val file = File(fileDirectory).listFiles()?.get(0) ?: return ApkSimpleInfo("0", 0, 0)
        val metaData = resolveApkFile(file)
        val modifyTime = file.lastModified()
        return ApkSimpleInfo(metaData.versionName, metaData.versionCode, modifyTime)
    }

    private fun resolveApkFile(file: File): ApkMeta {
        val apkFile = ApkFile(file)
        val metaData = apkFile.apkMeta
        apkFile.close()
        return metaData
    }

    private fun getMaxVersion(): Int {
        val file = File(apkBasePath)
        if (file.exists().not() || file.isDirectory.not() || file.listFiles().isNullOrEmpty()) return 0
        var maxVersionCode = 0
        file.listFiles()?.forEach {
            if (it.isFile) return@forEach
            val code = it.name.toInt()
            maxVersionCode = if (maxVersionCode < code) code else maxVersionCode
        }
        return maxVersionCode
    }
}