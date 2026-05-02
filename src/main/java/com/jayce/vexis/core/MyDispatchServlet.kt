package com.jayce.vexis.core

import com.jayce.vexis.foundation.Log
import com.jayce.vexis.foundation.socket.EventCenter
import com.jayce.vexis.foundation.utils.FileHelper
import com.jayce.vexis.foundation.utils.RedisUtil
import org.apache.ibatis.session.SqlSessionFactory
import org.springframework.context.ApplicationContext
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.servlet.DispatcherServlet

open class MyDispatchServlet : DispatcherServlet() {

    private val log by lazy { Log(this::class.java) }

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
        initMybatis(context)
        initRedis(context)
        initSocket(context)
        initProperties()
        FileHelper.init()
        log.d("应用全局环境：$applicationContext")
        log.d("数据连接工厂：$sqlSessionFactory")
        log.d("事件分发中心：$eventCenter")
    }

    private fun initMybatis(context: ApplicationContext) {
        if (applicationContext == null) {
            applicationContext = context
        }
        sqlSessionFactory = context.getBean(SqlSessionFactory::class.java)
    }

    private fun initRedis(context: ApplicationContext) {
        val redisTemplate = context.getBean("stringRedisTemplate") as StringRedisTemplate
        RedisUtil.init(redisTemplate, context)
    }

    private fun initSocket(context: ApplicationContext) {
        eventCenter = context.getBean(EventCenter::class.java)
        eventCenter?.start()
    }

    private fun initProperties() {
        baseFilePath = if (isLocalEnvironment()) {
            "D:/FileSystem/"
        } else {
            "/www/CreezenServer/FileSystem/"
        }
    }
}