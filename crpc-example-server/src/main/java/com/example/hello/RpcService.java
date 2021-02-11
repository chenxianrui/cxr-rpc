package com.example.hello;

import java.lang.annotation.*;

/**
 * @Author cxr
 * @Date 2021/1/25 14:47
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface RpcService {

    /**
     * Service version, default value is empty string
     */
    String version() default "";

    /**
     * Service group, default value is empty string
     */
    String group() default "";

}
