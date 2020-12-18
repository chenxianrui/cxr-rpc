package com.example.rpcdemo.agent.tagent.jdka.proxy;


import com.example.rpcdemo.agent.tagent.jdka.DebugInvocationHandler;

import java.lang.reflect.Proxy;


/**
 * @Author cxr
 * @Date 2020/12/18 11:38
 *
 * 获取代理对象的工厂类
 */
public class JdkProxyFactory {
    public static Object getProxy(Object target) {
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(), // 目标类的类加载
                target.getClass().getInterfaces(),  // 代理需要实现的接口，可指定多个
                new DebugInvocationHandler(target)   // 代理对象对应的自定义 InvocationHandler
        );
    }
}
