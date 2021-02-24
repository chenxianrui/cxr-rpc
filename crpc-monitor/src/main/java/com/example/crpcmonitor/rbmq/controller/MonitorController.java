package com.example.crpcmonitor.rbmq.controller;

import com.example.crpcmonitor.monitor.zk.ServiceMonitorImpl;
import com.example.crpcmonitor.rbmq.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @Author cxr
 * @Date 2021/2/21 13:19
 */
@RestController
public class MonitorController {

    @Autowired
    private WebSocketServer webSocketServer;

    @Resource
    private ServiceMonitorImpl serviceMonitor;

    @PostMapping("/sendAllWebSocket")
    public String test() throws Exception {
        String text="你们好！这是websocket群体发送！";
        serviceMonitor.watchChild();
        return text;
    }
}
