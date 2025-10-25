package com.jayce.vexis.business.controllers

import com.creezen.commontool.bean.ApkSimpleInfo
import com.jayce.vexis.core.MyDispatchServlet
import com.creezen.commontool.getRandomString
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

    private val apkBasePath = "${BASE_FILE_PATH}APK/"

    @RequestMapping(value = ["/uploadApk"])
    @ResponseBody
    fun uploadApk(@RequestParam("apkFile")  file: MultipartFile): Boolean {
        val tempName = getRandomString(20)
        val tempPath = "$apkBasePath$tempName.apk"
        val tempFile = File(tempPath)
        file.transferTo(tempFile)
        try {
            val metaData = resolveApkFile(tempFile)
            val versionName = metaData.versionName
            val versionCode = metaData.versionCode
            val destFile = File("$apkBasePath$versionCode")
            if (destFile.exists().not()) {
                if (versionCode <= getMaxVersion()) {
                    tempFile.delete()
                    return false
                }
                destFile.mkdir()
                tempFile.copyTo(File("${destFile.path}/$versionName.apk"))
                tempFile.delete()
                return true
            } else {
                tempFile.delete()
                return false
            }
        } finally {
            tempFile.delete()
        }
    }

    @RequestMapping(value = ["/checkVersion"])
    @ResponseBody
    fun checkVersion(): ApkSimpleInfo {
        val maxVersion = getMaxVersion()
        val fileDirectory = "${apkBasePath}$maxVersion"
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