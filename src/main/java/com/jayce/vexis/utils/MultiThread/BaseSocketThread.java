package com.jayce.vexis.utils.MultiThread;

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class BaseSocketThread extends Thread{
    protected String userName;
    protected static Socket socket;
    protected BufferedReader bufferedReader;
    protected BufferedWriter bufferedWriter;

    protected static JedisConnectionFactory jedisConnectionFactory;
    protected static StringRedisTemplate stringRedisTemplate;

    public final static Object lock=new Object();

    public BaseSocketThread(String userName, Socket socket) {
        this.userName = userName;
        this.socket=socket;
        try {
            this.bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
            this.bufferedWriter=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void set(JedisConnectionFactory connectionFactory,StringRedisTemplate redisTemplate){
        jedisConnectionFactory=connectionFactory;
        stringRedisTemplate=redisTemplate;
    }

    protected void handleMsg() throws IOException{
        while (true) {
            if(null==socket||socket.isClosed()||socket.isInputShutdown()) break;
            String msg = bufferedReader.readLine();
            if (null == msg || msg.length() == -1) break;
            System.out.println(msg);
        }
    }

    protected void onFinish(String threadMsg){
        synchronized (lock){
            try {
                if(null!=socket){
                    if(!socket.isClosed()) socket.close();
                    bufferedReader.close();
                    bufferedWriter.close();
                    socket=null;
                    System.out.printf("这里是%s，所有信息已关闭...再见，%s\n",threadMsg,userName);
                }
                Map<String,String> map=new HashMap<>();
                map.put("close",System.currentTimeMillis()+":##FINISH##");
                stringRedisTemplate.opsForStream().add("msg",map);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
