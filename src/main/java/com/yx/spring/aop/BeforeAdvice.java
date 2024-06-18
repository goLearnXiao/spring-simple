package com.yx.spring.aop;

import com.yx.spring.iface.*;

import java.lang.reflect.Method;

/**
 * @author yangxiao
 * @date 2021/6/5 15:35
 */
public class BeforeAdvice implements MethodInterceptor, Advice {
    private Method aspectJAdviceMethod;
    private AspectInstanceFactory aspectInstanceFactory;

    public BeforeAdvice(Method aspectJAdviceMethod, AspectInstanceFactory aspectInstanceFactory) {
        this.aspectJAdviceMethod = aspectJAdviceMethod;
        this.aspectInstanceFactory = aspectInstanceFactory;

    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        before();
        return invocation.proceed();
    }

    public void before () throws Throwable {
        // 这段代码应该抽象出去
        int parameterCount = this.aspectJAdviceMethod.getParameterCount();
        Object[] args = new Object[parameterCount];
        // 需要补充连接点，环绕通知...
        this.aspectJAdviceMethod.invoke(this.aspectInstanceFactory.getAspectInstance(), args);
    }


}
