package com.example.rpcdemo.utils.concurrent.threadpool;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @Author cxr
 * @Date 2020/12/19 19:44
 *
 * 线程池自定义配置类，可自行根据业务场景修改配置参数。
 */
@Getter
@Setter
public class CustomThreadPoolConfig {

    private static final int DEFAULT_CORE_POOL_SIZE = 10;                   // 默认连接数量
    private static final int DEFAULT_MAXIMUM_POOL_SIZE_SIZE = 100;          // 最大连接数量
    private static final int DEFAULT_KEEP_ALIVE_TIME = 1;                   // 默认存活时间
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;     // 默认时间单位
    private static final int DEFAULT_BLOCKING_QUEUE_CAPACITY = 100;         // 默认正在阻塞的队列容量
    private static final int BLOCKING_QUEUE_CAPACITY = 100;                 // 正在阻塞队列容量

    /**
     * 可配置参数
     */
    private int corePoolSize = DEFAULT_CORE_POOL_SIZE;
    private int maximumPoolSize = DEFAULT_MAXIMUM_POOL_SIZE_SIZE;
    private long keepAliveTime = DEFAULT_KEEP_ALIVE_TIME;
    private TimeUnit unit = DEFAULT_TIME_UNIT;

    // 使用有界队列
    private BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);

}
