import org.apache.log4j.spi.Filter
import org.apache.log4j.spi.LoggingEvent

class Log4jFilter : Filter() {
    override fun decide(p0: LoggingEvent?): Int {
        if (p0 == null) return ACCEPT
        val message = p0.loggerName
        if (message.toString().contains("RedisConnectionUtils")) {
            return DENY
        }
        return ACCEPT
    }
}