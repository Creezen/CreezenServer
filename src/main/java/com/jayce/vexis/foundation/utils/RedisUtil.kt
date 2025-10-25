package com.jayce.vexis.foundation.utils

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

object RedisUtil {

    const val STREAM_MESSAGE_ID = "msgId"
    const val STREAM_CONTENT_KEY = "messageKey"
    private const val STREAM_GROUP = "creezen"
    private const val STREAM_CONSUMER = "super"
    private const val STREAM_NAME = "telecom"
    private const val STREAM_READ_COUNT = 256L
    private const val STREAM_BLOCK_TIME = 2L

    private lateinit var stringOpt: ValueOperations<String, String>
    private lateinit var hashOpt: HashOperations<String, Any, Any>
    private lateinit var listOpt: ListOperations<String, String>
    private lateinit var setOpt: SetOperations<String, String>
    private lateinit var zsetOpt: ZSetOperations<String, String>
    private lateinit var streamOpt: StreamOperations<String, Any, Any>

    private val streamConsumer = Consumer.from(STREAM_GROUP, STREAM_CONSUMER)
    private val streamOption =  StreamReadOptions.empty().count(STREAM_READ_COUNT).block(Duration.ofMinutes(STREAM_BLOCK_TIME))
    private val streamOffset =  StreamOffset.create(STREAM_NAME, ReadOffset.lastConsumed())

    fun init(redisTemplate: StringRedisTemplate) {
        stringOpt = redisTemplate.opsForValue()
        hashOpt = redisTemplate.opsForHash()
        listOpt = redisTemplate.opsForList()
        setOpt = redisTemplate.opsForSet()
        zsetOpt = redisTemplate.opsForZSet()
        streamOpt = redisTemplate.opsForStream()
        createStreamGroup()
    }

    private fun createStreamGroup() {
        kotlin.runCatching {
            streamOpt.createGroup(STREAM_NAME, ReadOffset.from("0"), STREAM_GROUP)
        }.onFailure {
            println("Group exist!")
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <K, V> readStream(): List<MapRecord<String, K, V>> {
        val list = streamOpt.read(streamConsumer, streamOption, streamOffset)
        return list as List<MapRecord<String, K, V>>
    }

    fun <V> writeStream(content: V) {
        val map = mapOf(STREAM_CONTENT_KEY to content)
        streamOpt.add(STREAM_NAME, map)
    }

    fun ack(id: RecordId) {
        streamOpt.acknowledge(STREAM_NAME, STREAM_GROUP, id)
    }

}