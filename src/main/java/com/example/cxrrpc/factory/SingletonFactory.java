package com.example.cxrrpc.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author cxr
 * @Date 2020/12/19 20:04
 *
 * 获取单例对象的工厂类
 */
public class SingletonFactory {
    private static final Map<String, Object> OBJECT_MAP = new HashMap<>();

    private SingletonFactory(){

    }

    public static <T> T getInstance(Class<T> c){
        String key = c.toString();
        Object instance = null;
        if (instance == null){
            // 加锁
            synchronized (SingletonFactory.class){
                instance = OBJECT_MAP.get(key);
                if (instance == null){
                    try {
                        // 获取对象
                        instance = c.getDeclaredConstructor().newInstance();
                        // key：对象名称
                        // value：对象实例
                        OBJECT_MAP.put(key, instance);
                    } catch (IllegalAccessException | InstantiationException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    } catch (NoSuchMethodException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return c.cast(instance);
    }
}
