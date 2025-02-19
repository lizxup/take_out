package com.sky.handler;

import com.sky.exception.BaseException;
import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 这是个构造方法,根据传进来的  异常的类型 来选择构造方法
     *
     * 通过异常的方式处理 username(唯一) 重复的 异常
     *
     * 异常处理器捕获到 SQLIntegrityConstraintViolationException (数据库异常) 时候执行
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        String message = ex.getMessage();
        log.error("异常信息：{}", message);
        // 数据库报错 : Duplicate entry 'zhangsan' for key 'employee.idx_username'
        // 说明 zhangsan 这个username重复了 处理这个异常 只处理这个异常 其他的数据库异常  返回未知错误
        if (message.contains("Duplicate entry")){

            //分割错误信息 得到重复的username
            String[] split = message.split(" ");
            String username = split[2];

            //拼接成新的返回信息
            String msg = username + MessageConstant.ALREADY_EXISTS;
            return Result.error(msg);
        }else{
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }

}
