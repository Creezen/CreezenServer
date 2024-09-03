package cn.LJW.Utils;

import cn.LJW.Utils.MultiThread.BaseSocketThread;
import cn.LJW.Utils.MultiThread.SocketReader;
import cn.LJW.Utils.MultiThread.SocketWriter;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.net.Socket;

public class ThreadHelper {

    public static void startSocket(String name, Socket socket, StringRedisTemplate stringRedisTemplate, JedisConnectionFactory jedisConnectionFactory){
        BaseSocketThread.set(jedisConnectionFactory,stringRedisTemplate);
        new SocketReader(name,socket).start();
        new SocketWriter(name,socket).start();
    }
}
