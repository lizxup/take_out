package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解 ， 用于标识某个方法 需要进行 功能字段自动填充处理
 */


//说明该注解的使用范围(这个注解只能加载在方法上面)
@Target(ElementType.METHOD)

//说明该注解  什么时候生效(在运行时生效)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
//    通过枚举的方式 指定当前数据库操作的类型

    //数据库操作类型， updata insert  这两个值在OpreationType 枚举  中枚举了
    OperationType value();
}
