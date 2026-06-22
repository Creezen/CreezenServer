package com.jayce.vexis.util

import com.jayce.vexis.util.bean.FileType
import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor

object Util {

    fun loadDataFromYAML(): Map<String, List<String>> {
        kotlin.runCatching {
            val yaml = Yaml(Constructor(LoaderOptions()))
            val source = "/yaml/fileType.yaml"
            javaClass.getResourceAsStream(source)?.use {
                val values = yaml.loadAs(it, FileType::class.java)
                return values.typeMap
            } ?: run {
                println("stream is empty")
            }
        }.onFailure {
            println("fail ${it.message}")
        }
        return mapOf()
    }
}