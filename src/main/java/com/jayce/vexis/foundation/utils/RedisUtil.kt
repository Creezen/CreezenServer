package com.jayce.vexis.foundation.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.springframework.data.redis.connection.stream.Consumer
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.connection.stream.ReadOffset
import org.springframework.data.redis.connection.stream.RecordId
import org.springframework.data.redis.connection.stream.StreamOffset
import org.springframework.data.redis.connection.stream.StreamReadOptions
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.ListOperations
import org.springframework.data.redis.core.SetOperations
import org.springframework.data.redis.core.StreamOperations
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.data.redis.core.ZSetOperations
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

object RedisUtil {

    const val STREAM_MESSAGE_ID = "msgId"
    const val STREAM_CONTENT_KEY = "messageKey"
    const val STREAM_CONTENT_FINISH = "finishMessage"
    private const val STREAM_GROUP = "creezen"
    private const val STREAM_CONSUMER = "consumer"
    private const val STREAM_NAME = "telecom"
    private const val STREAM_READ_COUNT = 256L
    private const val STREAM_BLOCK_TIME = 2L

    private lateinit var stringOpt: ValueOperations<String, String>
    private lateinit var hashOpt: HashOperations<String, Any, Any>
    private lateinit var listOpt: ListOperations<String, String>
    private lateinit var setOpt: SetOperations<String, String>
    private lateinit var zsetOpt: ZSetOperations<String, String>
    private lateinit var streamOpt: StreamOperations<String, Any, Any>

    private val streamOption =  StreamReadOptions.empty().count(STREAM_READ_COUNT).block(Duration.ofMinutes(STREAM_BLOCK_TIME))
    private val streamOffset =  StreamOffset.create(STREAM_NAME, ReadOffset.lastConsumed())
    private val ackStreamOffset = StreamOffset.create(STREAM_NAME, ReadOffset.from("0"))
    private val consumerMap: ConcurrentHashMap<String, Consumer> = ConcurrentHashMap()

    fun init(redisTemplate: StringRedisTemplate) {
        stringOpt = redisTemplate.opsForValue()
        hashOpt = redisTemplate.opsForHash()
        listOpt = redisTemplate.opsForList()
        setOpt = redisTemplate.opsForSet()
        zsetOpt = redisTemplate.opsForZSet()
        streamOpt = redisTemplate.opsForStream()
    }

    fun createStreamGroupIfNeed(userId: String) {
        kotlin.runCatching {
            streamOpt.createGroup(STREAM_NAME, ReadOffset.from("0"), userId)
        }.onFailure {
            println("Group $userId exist!")
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

    fun sendFinishMsg(userId: String) {
        val map = mapOf(STREAM_CONTENT_FINISH to userId)
        streamOpt.add(STREAM_NAME, map)
    }

    fun ack(userId: String, id: RecordId) {
        streamOpt.acknowledge(STREAM_NAME, userId, id)
    }

}