package com.yx.spring.aop;

import com.yx.spring.YxApplicationContext;
import com.yx.spring.aop.proxy.ProxyFactory;
import com.yx.spring.iface.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author yangxiao
 * @date 2021/5/30 11:21
 */
public class AnnotationAwareAspectJAutoProxyCreator implements SmartInstantiationAwareBeanPostProcessor, ApplicationContextAware {
    private YxApplicationContext applicationContext;

    private final AspectJAdvisorFactory advisorFactory = new DefaultAspectJAdvisorFactory();

    // 缓存advisor
    private List<Advisor> cachedAdvisors;

    /**
     * 缓存正在创建的代理beanName
     */
    private final Set<String> earlyProxyReferences = new HashSet<>();

    @Override
    public void setApplicationContext(YxApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object getEarlyBeanReference(Object bean, String beanName) throws RuntimeException {
        this.earlyProxyReferences.add(beanName);
        return wrapIfNecessary(bean, beanName);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return SmartInstantiationAwareBeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean != null) {
            if (!this.earlyProxyReferences.contains(beanName)) {
                return wrapIfNecessary(bean, beanName);
            } else {
                this.earlyProxyReferences.remove(beanName);
            }
        }
        return bean;
    }


    private Object wrapIfNecessary(Object bean, String beanName) {
        if (isInfrastructureClass(bean.getClass())) {
            return bean;
        }
        List<Advisor> advisorList = findEligibleAdvisors(bean.getClass(), beanName);
        if (!advisorList.isEmpty()) {
            return createProxy(bean.getClass(), bean, beanName, advisorList);
        }
        return bean;
    }

    private List<Advisor> findEligibleAdvisors(Class<?> beanClass, String beanName) {
        List<Advisor> candidateAdvisors = findCandidateAdvisors();
        return findAdvisorsThatCanApply(candidateAdvisors, beanClass, beanName);
    }

    private List<Advisor> findAdvisorsThatCanApply(List<Advisor> candidateAdvisors, Class<?> beanClass, String beanName) {
        if (candidateAdvisors.isEmpty()) {
            return candidateAdvisors;
        }
        List<Advisor> eligibleAdvisors = new ArrayList<>(candidateAdvisors.size());
        Method[] methods = beanClass.getDeclaredMethods();

        for (Advisor advisor : candidateAdvisors) {
            MethodMatcher methodMatcher = advisor.getPointcut().getMethodMatcher();
            for (Method method : methods) {
                if (methodMatcher.matches(method, beanClass)) {
                    eligibleAdvisors.add(advisor);
                    break;
                }
            }
        }
        return eligibleAdvisors;
    }

    /**
     * 获取所有
     * @return
     */
    private List<Advisor> findCandidateAdvisors() {
        if (this.cachedAdvisors != null) {
            return this.cachedAdvisors;
        }
        List<Class<?>> allClass = applicationContext.getAllBeanClass();
        List<Advisor> advisors = new ArrayList<>();

        for (Class<?> cls : allClass) {
            if (this.advisorFactory.isAspect(cls)) {
                List<Advisor> classAdvisors = this.advisorFactory.getAdvisors(cls);
                advisors.addAll(classAdvisors);
            }
        }
        this.cachedAdvisors = advisors;
        return this.cachedAdvisors;
    }

    private Object createProxy(Class<?> targetClass, Object target, String beanName, List<Advisor> advisorList) {
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTargetSource(new DefaultTargetSource(target));
        proxyFactory.addAdvisors(advisorList);
        proxyFactory.setInterfaces(targetClass.getInterfaces());
        System.out.println(beanName + " 创建代理，advisor个数：" + advisorList.size());
        return proxyFactory.getProxy();
    }

    protected boolean isInfrastructureClass(Class<?> beanClass) {
        boolean retVal = Advice.class.isAssignableFrom(beanClass) ||
                Pointcut.class.isAssignableFrom(beanClass) ||
                Advisor.class.isAssignableFrom(beanClass) ||
                this.advisorFactory.isAspect(beanClass);
        if (retVal) {
            System.out.println("………………………………………………Did not attempt to auto-proxy infrastructure class [" + beanClass.getName() + "]");
        }
        return retVal;
    }


}
