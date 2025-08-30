package cn.LJW.Utils.MultiThread;

import org.springframework.data.redis.connection.stream.*;

import java.io.IOException;
import java.net.Socket;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class SocketWriter extends BaseSocketThread {

    public SocketWriter(String userName, Socket socket) {
        super(userName, socket);
    }

    @Override
    public void run() {
        Consumer consumer=Consumer.from(userName + "_group", "c1");
        List<MapRecord<String, Object, Object>> mapRecords;
        System.out.printf("你好，%s，这里是写线程...\n",userName);
        try {
            while(true){
                if(null==socket||socket.isClosed()||socket.isOutputShutdown()) break;
                System.out.println("wait stream");
                mapRecords = stringRedisTemplate.opsForStream().read(consumer,
                        StreamReadOptions.empty().count(1).block(Duration.ofMillis(50000)),
                        StreamOffset.create("msg", ReadOffset.lastConsumed()));
                for (MapRecord<String, Object, Object> mapRecord : mapRecords) {
                    Map<Object, Object> value = mapRecord.getValue();
                    RecordId id = mapRecord.getId();
                    stringRedisTemplate.opsForStream().acknowledge("msg",userName + "_group",id);
                    String res = (String)value.get(userName);
                    System.out.printf("%s: %s\n",id,res);
                }
                bufferedWriter.write("写数据..."+"\n");
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            System.out.println("写线程报错了...");
        }finally {
            onFinish("写线程");
        }
        System.out.println("写线程结束...");
    }
}