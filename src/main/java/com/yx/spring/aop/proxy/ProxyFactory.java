package com.yx.spring.aop.proxy;

import com.yx.spring.iface.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yangxiao
 * @date 2021/6/5 9:28
 */
public class ProxyFactory {
    private List<Advisor> advisorList;
    private TargetSource targetSource;
    private List<Class<?>> interfaces;
    private boolean proxyTargetClass;

    public ProxyFactory() {
        this.proxyTargetClass = false;
        this.advisorList = new ArrayList<>();
        this.interfaces = new ArrayList<>();
    }

    public void setInterfaces(Class<?>... interfaces) {
        this.interfaces.clear();
        for (Class<?> intf : interfaces) {
            if (!intf.isInterface()) {
                throw new IllegalArgumentException(intf.getName() + " is not an interface");
            }
            if (!this.interfaces.contains(intf)) {
                this.interfaces.add(intf);
            }
        }
    }

    public void setTargetSource(TargetSource targetSource) {
        this.targetSource = targetSource;
    }

    public void addAdvisors(List<Advisor> advisorList) {
        this.advisorList.addAll(advisorList);
    }

    public Object getProxy() {
        AopProxy aopProxy = createAopProxy();
        return aopProxy.getProxy();
    }


    public AopProxy createAopProxy() {
        // 这里只实现jdk动态代理
        if (!this.interfaces.isEmpty()){
            return new JdkDynamicAopProxy(this);
        }
        throw new RuntimeException("暂不支持除了jdk代理的其他代理");
    }

    public TargetSource getTargetSource() {
        return targetSource;
    }

    /**
     * 得到通知
     * @param method
     * @param targetClass
     * @return
     */
    public List<Interceptor> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) {
        List<Interceptor> interceptorList = new ArrayList<>(this.advisorList.size());
        for (Advisor advisor : this.advisorList) {
            MethodMatcher methodMatcher = advisor.getPointcut().getMethodMatcher();
            if (methodMatcher.matches(method, targetClass)) {
                Advice advice = advisor.getAdvice();
                if (advice instanceof MethodInterceptor) {
                    interceptorList.add((MethodInterceptor) advice);
                }
            }
        }
        return interceptorList;
    }

    public Class<?>[] getProxiedInterfaces() {
        Class<?>[] emptyArr = {};
        return this.interfaces.toArray(emptyArr);
    }


}
