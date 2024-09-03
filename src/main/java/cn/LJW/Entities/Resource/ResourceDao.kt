package cn.LJW.Entities.Resource

interface ResourceDao {

    fun insertFile(file: FileBean)

    fun getFile(): List<FileBean>
}