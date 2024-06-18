package com.yx.spring.aop;

import com.yx.spring.iface.Advice;
import com.yx.spring.iface.Advisor;
import com.yx.spring.iface.Pointcut;

/**
 * @author yangxiao
 * @date 2021/6/5 14:40
 */
public class DefaultPointcutAdvisor implements Advisor {

    private Pointcut pointcut;
    private Advice advice;

    public DefaultPointcutAdvisor(Pointcut pointcut, Advice advice) {
        this.pointcut = pointcut;
        this.advice = advice;
    }

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    public Advice getAdvice() {
        return advice;
    }

}
