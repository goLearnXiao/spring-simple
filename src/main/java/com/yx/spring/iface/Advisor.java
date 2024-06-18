package com.yx.spring.iface;

/**
 * @author yangxiao
 * @date 2021/6/5 14:25
 */
public interface Advisor {

    Advice getAdvice();

    Pointcut getPointcut();
}
