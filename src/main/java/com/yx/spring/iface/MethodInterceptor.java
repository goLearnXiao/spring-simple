package com.yx.spring.iface;

/**
 * @author yangxiao
 * @date 2021/6/5 15:39
 */
public interface MethodInterceptor extends Interceptor{

    Object invoke(MethodInvocation invocation) throws Throwable;

}
