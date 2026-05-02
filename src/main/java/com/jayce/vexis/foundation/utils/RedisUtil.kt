package com.jayce.vexis.foundation.utils

import com.creezen.commontool.Config.EventType.EVENT_TYPE_FINISH
import com.creezen.commontool.bean.TelecomBean
import com.creezen.commontool.toJson
import com.jayce.vexis.foundation.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.context.ApplicationContext
import org.springframework.data.redis.connection.stream.*
import org.springframework.data.redis.core.*
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

object RedisUtil {

    const val STREAM_MESSAGE_ID = "msgId"
    const val STREAM_CONTENT_KEY = "messageKey"
    private const val STREAM_CONSUMER = "consumer"
    private const val STREAM_NAME = "telecom"
    private const val ONLINE_PREFIX = "ONLINE_"
    private const val STREAM_READ_COUNT = 256L
    private const val STREAM_BLOCK_TIME = 2L

    private val log by lazy { Log(this::class.java) }

    private lateinit var template: StringRedisTemplate
    private lateinit var stringOpt: ValueOperations<String, String>
    private lateinit var hashOpt: HashOperations<String, Any, Any>
    private lateinit var listOpt: ListOperations<String, String>
    private lateinit var setOpt: SetOperations<String, String>
    private lateinit var zsetOpt: ZSetOperations<String, String>
    private lateinit var streamOpt: StreamOperations<String, Any, Any>
    private lateinit var geoOpt: GeoOperations<String, Any>
    private lateinit var hyperLogOpt: HyperLogLogOperations<String, Any>
    private lateinit var clusterOpt: ClusterOperations<String, Any>

    private val streamOption =  StreamReadOptions.empty().count(STREAM_READ_COUNT).block(Duration.ofMinutes(STREAM_BLOCK_TIME))
    private val streamOffset =  StreamOffset.create(STREAM_NAME, ReadOffset.lastConsumed())
    private val ackStreamOffset = StreamOffset.create(STREAM_NAME, ReadOffset.from("0"))
    private val consumerMap: ConcurrentHashMap<String, Consumer> = ConcurrentHashMap()

    /**
     * 所有需要通过aop代理的数据，都需要通过spring管理
     * 所以除了定义切点之外，还需要定义bean，以及逐一将切点都加进去
     */
    fun init(redisTemplate: StringRedisTemplate, context: ApplicationContext) {
        initOpt(redisTemplate, context)
        initStatus()
    }

    @Suppress("UNCHECKED_CAST")
    private fun initOpt(redisTemplate: StringRedisTemplate, context: ApplicationContext) {
        template = redisTemplate
        stringOpt = context.getBean("stringOpt") as ValueOperations<String, String>
        hashOpt = context.getBean("hashOpt") as HashOperations<String, Any, Any>
        listOpt = context.getBean("listOpt") as ListOperations<String, String>
        setOpt = context.getBean("setOpt") as SetOperations<String, String>
        zsetOpt = context.getBean("zsetOpt") as ZSetOperations<String, String>
        streamOpt = context.getBean("streamOpt") as StreamOperations<String, Any, Any>
        geoOpt = context.getBean("geoOpt") as GeoOperations<String, Any>
        hyperLogOpt = context.getBean("hyperLogOpt") as HyperLogLogOperations<String, Any>
        clusterOpt =  context.getBean("clusterOpt") as ClusterOperations<String, Any>
    }

    private fun initStatus() {
        clearOnlineStatus()
    }

    fun createStreamGroupIfNeed(userId: String) {
        kotlin.runCatching {
            streamOpt.createGroup(STREAM_NAME, ReadOffset.from("0"), userId)
        }.onFailure {
            log.d("Group $userId exist!")
        }
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun <K, V> readStream(userId: String, ack: AtomicBoolean): List<MapRecord<String, K, V>> {
        return withContext(Dispatchers.IO) {
            val consumer = consumerMap.getOrPut(userId) {
                Consumer.from(userId, STREAM_CONSUMER)
            }
            val finalList = arrayListOf<MapRecord<String, Any, Any>>()
            if (ack.get()) {
                ack.set(false)
                finalList.addAll(streamOpt.read(consumer, streamOption, ackStreamOffset) ?: listOf())
            }
            val list = streamOpt.read(consumer, streamOption, streamOffset)
            finalList.addAll(list ?: listOf())
            finalList as List<MapRecord<String, K, V>>
        }
    }

    fun <V> writeStream(content: V) {
        val map = mapOf(STREAM_CONTENT_KEY to content)
        streamOpt.add(STREAM_NAME, map)
    }

    fun sendFinishMsg(userId: String, msg: TelecomBean? = null) {
        val finishJson = TelecomBean(
            type = EVENT_TYPE_FINISH,
            userId = userId,
            nickName = msg?.nickName ?: "",
            session = msg?.session ?: ""
        ).toJson()
        writeStream(finishJson)
    }

    fun ack(userId: String, id: RecordId) {
        streamOpt.acknowledge(STREAM_NAME, userId, id)
    }

    fun setOnlineStatus(userId: String, session: String) {
        stringOpt.set(getOnlineKey(userId), session)
    }

    fun isUserAlreadyOnline(userId: String): Boolean {
        val session = stringOpt.get(getOnlineKey(userId))
        return session != null
    }

    fun verifyOnlineStatus(msg: TelecomBean): Boolean {
        val cacheSession = stringOpt.get(getOnlineKey(msg.userId))
        log.d("校验session 缓存：$cacheSession 新：${msg.session}")
        if (cacheSession == null || cacheSession == msg.session) {
            return true
        }
        return false
    }

    fun verifyOnlineStatus(userId: String?, session: String?): Boolean {
        if (userId == null) return false
        if (session == null) return false
        val cacheSession = stringOpt.get(getOnlineKey(userId))
        return cacheSession == session
    }

    fun setOfflineStatus(userId: String) {
        template.delete(getOnlineKey(userId))
    }

    private fun clearOnlineStatus() {
        template.keys("*").forEach {
            if (it.startsWith(ONLINE_PREFIX)) {
                template.delete(it)
            }
        }
    }

    private fun getOnlineKey(userId: String) = "$ONLINE_PREFIX$userId"
}