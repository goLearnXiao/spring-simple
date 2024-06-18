package com.yx.spring.aop.proxy;

import com.yx.spring.aop.DefaultMethodInvocation;
import com.yx.spring.iface.AopProxy;
import com.yx.spring.iface.Interceptor;
import com.yx.spring.iface.TargetSource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @author yangxiao
 * @date 2021/6/5 9:45
 */
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {

    private ProxyFactory proxyFactory;

    public JdkDynamicAopProxy(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    @Override
    public Object getProxy() {
        Class<?>[] proxiedInterfaces = proxyFactory.getProxiedInterfaces();
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), proxiedInterfaces, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        TargetSource targetSource = this.proxyFactory.getTargetSource();
        Object target = null;
        Object result;

        target = targetSource.getTarget();
        Class<?> targetClass = target.getClass();

        List<Interceptor> chain = this.proxyFactory.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);

        if (chain.isEmpty()) {
            result = method.invoke(target, args);
        } else {
            DefaultMethodInvocation methodInvocation = new DefaultMethodInvocation(target, method, args, chain);
            result = methodInvocation.proceed();
        }

        //处理特殊值 this
        Class<?> returnType = method.getReturnType();
        if (result != null && result == target &&
                returnType != Object.class && returnType.isInstance(proxy)) {
            result = proxy;
        }
        return result;
    }
}
