package cn.LJW

import cn.LJW.Controllers.EventCenter
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
        protected var sqlSessionFactory: SqlSessionFactory? = null
        protected var applicationContext: ApplicationContext? = null
        @JvmStatic
        var redisTemplate: StringRedisTemplate? = null

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
            throw IllegalArgumentException("UNKNOW MACHIE!!!")
        }
        println("current environment:  $environmentType")
    }

    private fun initMybatis(context: ApplicationContext) {
        if (applicationContext == null) {
            applicationContext = context
        }
        if(sqlSessionFactory != null) {
            return
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
        redisTemplate = context.getBean(StringRedisTemplate::class.java)
    }

    private fun initSocket(context: ApplicationContext) {
        eventCenter = context.getBean(EventCenter::class.java)
        eventCenter?.beginSocket()
    }
}