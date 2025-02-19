package com.sky.context;


/**
 * 使用BaseContext 包装threadClocal类
 */
public class BaseContext {


    //threadLocal 不是thread 而是 thread 的一个局部变量 是为每一个线程提供独一份的存储空间
    //具有隔离效果 只有在线程内才能获取对应的值
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 将id放入 threadLocal
     * @param id
     */
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    /**
     * 通过 threadLocal 获得 id
     * @return
     */
    public static Long getCurrentId() {
        return threadLocal.get();
    }

    /**
     * 删除 threadLocal中的id
     */
    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
