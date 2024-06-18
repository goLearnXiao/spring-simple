package com.yx.spring.aop;

import com.yx.spring.iface.Advice;
import com.yx.spring.iface.AspectInstanceFactory;
import com.yx.spring.iface.MethodInterceptor;
import com.yx.spring.iface.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author yangxiao
 * @date 2021/6/5 15:36
 */
public class AfterAdvice implements MethodInterceptor, Advice {
    private Method aspectJAdviceMethod;
    private AspectInstanceFactory aspectInstanceFactory;

    public AfterAdvice(Method aspectJAdviceMethod, AspectInstanceFactory aspectInstanceFactory) {
        this.aspectJAdviceMethod = aspectJAdviceMethod;
        this.aspectInstanceFactory = aspectInstanceFactory;

    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        try {
            return invocation.proceed();
        } finally {
            after();
        }
    }

    public void after() throws Throwable {
        int parameterCount = this.aspectJAdviceMethod.getParameterCount();
        Object[] args = new Object[parameterCount];
        //需要补充连接点，环绕通知...
        this.aspectJAdviceMethod.invoke(this.aspectInstanceFactory.getAspectInstance(), args);
    }


}
