package com.yx.spring.aop;

import com.yx.spring.iface.Interceptor;
import com.yx.spring.iface.MethodInterceptor;
import com.yx.spring.iface.MethodInvocation;

import java.lang.reflect.Method;
import java.util.List;

public class DefaultMethodInvocation implements MethodInvocation {

    private Object target;
    private Method method;
    private Object[] args;
    List<?> methodInterceptorList;
    // 调用位置
    private int currentInterceptorIndex = -1;

    public DefaultMethodInvocation(Object target, Method method, Object[] args, List<Interceptor> methodInterceptorList) {
        this.target = target;
        this.method = method;
        if (args == null) {
            this.args = new Object[0];
        } else {
            this.args = args;
        }
        this.methodInterceptorList = methodInterceptorList;
    }

    @Override
    public Object proceed() throws Throwable {
        if (this.currentInterceptorIndex == this.methodInterceptorList.size() - 1) {
            return invokeJoinpoint();
        }
        Object methodInterceptor = this.methodInterceptorList.get(++currentInterceptorIndex);
        return ((MethodInterceptor) methodInterceptor).invoke(this);
    }

    protected Object invokeJoinpoint() throws Throwable {
        return method.invoke(target, args);
    }

    @Override
    public Object[] getArguments() {
        return this.args;
    }

    @Override
    public void setArguments(Object[] args) {
        this.args = args;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }
}
