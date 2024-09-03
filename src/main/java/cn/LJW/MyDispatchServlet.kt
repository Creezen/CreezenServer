package cn.LJW

import cn.LJW.Controllers.EventCenter
import org.apache.ibatis.io.Resources
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.getBean
import org.springframework.context.ApplicationContext
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.servlet.DispatcherServlet
import java.io.IOException
import java.net.ServerSocket

open class MyDispatchServlet : DispatcherServlet() {

    companion object {
        @JvmStatic
        protected var sqlSessionFactory: SqlSessionFactory? = null
        protected var applicationContext: ApplicationContext? = null
        @JvmStatic
        var redisTemplate: StringRedisTemplate? = null
    }

    private var eventCenter: EventCenter? = null

    override fun initStrategies(context: ApplicationContext) {
        super.initStrategies(context)
        initMybatis(context)
        initRedis(context)
        initSocket(context)
        println("应用全局环境：$applicationContext\n" +
                "数据连接工厂：$sqlSessionFactory\n" +
                "事件分发中心：$eventCenter")
    }

    private fun initMybatis(context: ApplicationContext) {
        if (applicationContext == null) {
            applicationContext = context
        }
        if(sqlSessionFactory != null) {
            return
        }
        try {
            sqlSessionFactory = SqlSessionFactoryBuilder().build(
                    Resources.getResourceAsStream("Mybatis/MybatisConfig.xml")
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