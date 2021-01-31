package com.example.cxrrpc.demo.agent.tagent.jdka;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Author cxr
 * @Date 2020/12/18 11:27
 *
 * 定义一个jdk动态代理类
 */
public class DebugInvocationHandler implements InvocationHandler{

    /**
     * 代理类中真正的对象
     */
    private final Object target;

    public DebugInvocationHandler(Object target){
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
        // 调用方法前，我们可以添加自己的操作
        System.out.println("在方法" + method.getName() + "之前");
        Object result = method.invoke(target, args);
        System.out.println("在方法" + method.getName() + "之后");
        return result;
    }
}
