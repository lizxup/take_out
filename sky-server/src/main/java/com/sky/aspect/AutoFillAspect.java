package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 *
 * 员工管理 和 分类套餐管理  的代码部分在这里之前开发的   这里的公共字段 统一操作   功能包含了 之前的一部分功能
 * 为了方便以后学习前面的 重复功能部分的代码就没有删除
 *
 * 自定义切面，实现公共字段自动填充处理逻辑
 */

@Component
//标记是切面类
@Aspect
@Slf4j


public class AutoFillAspect {

    //切面 =  切入点 + 通知

    /**
     * 切入点 （对那些类，的哪些方法进行拦截）
     */



//    @Pointcut 是一个注解，用于在面向切面编程（AOP）中指定切入点   （对那些类，的哪些方法进行拦截）
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    //拦截 com.sky.mapper.*(所有类).*(所有方法)(..)(匹配所有的参数类型) && (还需要当前的方法上加上了AutoFill注解)
    public void autoFillPointCut(){
    }

    /**定义通知（前置通知    因为需要 在查数据库前 对 对象的数据填充）
     *  在通知中为公共字段进行赋值
     */
    //    绑定切点，在切点执行前 执行
    @Before("autoFillPointCut()")
    //JojnPoin 连接点类，在哪里进行连接
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行公共字段自动填充...");

        //获取当前被拦截的方法上的数据库操作类型
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//方法签名对象

        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获得方法上的注解对象

        OperationType operationType = autoFill.value();//获得数据库操作类型

        //获取到被拦截到的方法上的参数（实体对象）
        Object[] args = joinPoint.getArgs();//执行到切点所标记方法的时候,获得该方法的所有参数


        //防止出现空指针
        if (args == null || args.length == 0){
            return;
        }

        //默认获得第一个参数
        Object entity = args[0];

        //获取赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();


        //根据当前的操作类型，为对应的属性通过反射来赋值
        if (operationType == OperationType.INSERT){
            try {

                //获得entity对象的四个set方法。使用常量的目的是为了 规范
                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射 为对象类型属性赋值
                setCreateTime.invoke(entity,now);
                setUpdateTime.invoke(entity,now);
                setCreateUser.invoke(entity,currentId);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        else if (operationType == OperationType.UPDATE){
            try {

                Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);

                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                //通过反射 为对象类型属性赋值
                setUpdateTime.invoke(entity,now);
                setUpdateUser.invoke(entity,currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
