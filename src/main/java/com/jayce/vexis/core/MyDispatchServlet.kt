package com.jayce.vexis.core

import com.jayce.vexis.foundation.socket.EventCenter
import com.jayce.vexis.foundation.utils.FileHelper
import com.jayce.vexis.foundation.utils.RedisUtil
import org.apache.ibatis.io.Resources
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.springframework.context.ApplicationContext
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.servlet.DispatcherServlet
import java.io.IOException

open class MyDispatchServlet : DispatcherServlet() {

    companion object {
        private var environmentType: Int = -1
        @JvmStatic
        protected lateinit var sqlSessionFactory: SqlSessionFactory
        protected var applicationContext: ApplicationContext? = null

        private var baseFilePath: String = ""
        val BASE_FILE_PATH: String
            get() = baseFilePath

        fun isLocalEnvironment(): Boolean {
            return environmentType == 1
        }
    }

    private var eventCenter: EventCenter? = null

    override fun initStrategies(context: ApplicationContext) {
        super.initStrategies(context)
        getEnv()
        initMybatis(context)
        initRedis(context)
        initSocket(context)
        initProperties()
        FileHelper.init()
        println("应用全局环境：$applicationContext\n" +
                "数据连接工厂：$sqlSessionFactory\n" +
                "事件分发中心：$eventCenter")
    }

    private fun getEnv() {
        val env = System.getenv()["CreezenEnv"]
        if (env == "LOCAL_MACHINE") {
            environmentType = 1
        } else if (env == "CLOUD_MACHINE") {
            environmentType = 2
        } else {
            throw IllegalArgumentException("UNKNOWN MACHINE!!!")
        }
        println("current environment:  $environmentType")
    }

    private fun initMybatis(context: ApplicationContext) {
        if (applicationContext == null) {
            applicationContext = context
        }
        try {
            val environment = if (isLocalEnvironment()) {
                "LOCAL"
            } else {
                "CLOUD"
            }
            println("Mybatis config: $environment")
            sqlSessionFactory = SqlSessionFactoryBuilder().build(
                    Resources.getResourceAsStream("Mybatis/MybatisConfig.xml"), environment
                )
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun initRedis(context: ApplicationContext) {
        val redisTemplate = context.getBean(StringRedisTemplate::class.java)
        RedisUtil.init(redisTemplate)
    }

    private fun initSocket(context: ApplicationContext) {
        eventCenter = context.getBean(EventCenter::class.java)
        eventCenter?.start()
    }

    private fun initProperties() {
        if (isLocalEnvironment()) {
            baseFilePath = "D:/FileSystem/"
        } else {
            baseFilePath = "/www/CreezenServer/FileSystem/"
        }
    }
}