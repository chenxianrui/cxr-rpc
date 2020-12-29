package com.example.rpcdemo.config;

import com.example.rpcdemo.register.zk.util.CuratorUtils;
import com.example.rpcdemo.utils.concurrent.threadpool.ThreadPoolFactoryUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author cxr
 * @Date 2020/12/19 20:35
 *
 * 当服务器关闭时，执行一些操作，例如注销所有服务的注册
 */
@Slf4j
public class CustomShutdownHook {

    private static final CustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new CustomShutdownHook();

    public static CustomShutdownHook getCustomShutdownHook(){
        return CUSTOM_SHUTDOWN_HOOK;
    }

    /**
     * 清楚所有注册服务
     */
    public void clearAll(){
        log.info("清除所有注册服务");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            CuratorUtils.clearRegistry(CuratorUtils.getZkClient());
            ThreadPoolFactoryUtils.shutDownAllThreadPool();
        }));
    }
}
