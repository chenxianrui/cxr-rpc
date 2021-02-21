package com.example.crpcmonitor.rbmq;

import com.example.crpcmonitor.rbmq.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author dingchengxiang
 * @Description //TODO 消息定时器，通知websocket
 * @Date 14:56 2019/11/12
 * @Param
 * @return
 **/
@Component
public class Task {
    @Autowired
    private WebSocketServer webSocketServer;
    /**
     * @throws Exception
     */
    @Scheduled(cron="0/5 * *  * * ? ")
    public void JqcaseSearch() {
        try {
            System.out.println("这是心跳");
            webSocketServer.sendInfo("主动推送消息");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
