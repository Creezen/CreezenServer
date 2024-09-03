package cn.LJW.Entities.Resource

data class FileBean(
    val fileName: String,
    val fileID: String,
    val fileSuffix: String,
    val description: String,
    val illustrate: String,
    val fileSize: Long,
    val uploadTime: String,
)
