package com.jayce.vexis.utils.MultiThread;

import java.io.*;
import java.net.Socket;

public class SocketReader extends BaseSocketThread {

    public SocketReader(String userName, Socket socket) {
        super(userName, socket);
    }

    @Override
    public void run() {
        System.out.printf("你好，%s，这里是读线程...\n",userName);
        try {
            handleMsg();
        } catch (IOException e) {
            System.out.println("读线程报错了...");
        }finally {
            onFinish("读线程");
        }
        System.out.println("读线程结束...");
    }
}
